package com.weplay.sync.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import com.weplay.sync.shared.protocol.AudioPayloadPacket
import com.weplay.sync.shared.protocol.HandshakeAcceptPacket
import com.weplay.sync.shared.protocol.HandshakeRequestPacket
import com.weplay.sync.shared.protocol.PacketType
import com.weplay.sync.shared.protocol.TimeSyncPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentSkipListMap

enum class ReceiverState {
    DISCONNECTED,
    CONNECTING,
    HANDSHAKE_ACCEPTED,
    STREAMING,
    SESSION_CLOSED,
    ERROR
}

data class ReceiverTelemetry(
    val state: ReceiverState = ReceiverState.DISCONNECTED,
    val connectedHost: String? = null,
    val packetsReceived: Long = 0L,
    val packetsDropped: Long = 0L,
    val bufferFillCount: Int = 0,
    val jitterMs: Double = 0.0,
    val rttMs: Double = 0.0,
    val errorMessage: String? = null
)

class AudioReceiverEngine(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var socket: DatagramSocket? = null
    private var audioTrack: AudioTrack? = null

    private val _telemetry = MutableStateFlow(ReceiverTelemetry())
    val telemetry: StateFlow<ReceiverTelemetry> = _telemetry.asStateFlow()

    @Volatile
    private var isRunning = false
    private var sessionToken: Int = 0
    private var hostAddress: InetAddress? = null
    private var hostPort: Int = 48200

    // User-adjustable lip sync delay in milliseconds (0 to 100ms)
    var lipSyncDelayMs: Int = 0

    // Jitter buffer ordered by monotonic sequence number
    private val jitterBuffer = ConcurrentSkipListMap<Long, ByteArray>()
    private var expectedSequence: Long = -1L

    fun connect(hostIp: String, sessionCode: String, listenerName: String = Build.MODEL) {
        if (isRunning) disconnect()

        _telemetry.value = ReceiverTelemetry(state = ReceiverState.CONNECTING)
        isRunning = true

        scope.launch {
            try {
                val targetHost = InetAddress.getByName(hostIp)
                hostAddress = targetHost
                socket = DatagramSocket().apply {
                    soTimeout = 3000
                }

                // 1. Send Handshake Request
                val handshakeReq = HandshakeRequestPacket(
                    sessionCode = sessionCode,
                    listenerName = listenerName,
                    deviceTypeCode = 0 // Headphones
                ).toBinary()

                val handshakePacket = DatagramPacket(handshakeReq, handshakeReq.size, targetHost, hostPort)
                socket?.send(handshakePacket)

                // 2. Await Handshake Accept
                val receiveBuffer = ByteArray(2048)
                val responsePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
                socket?.receive(responsePacket)

                val accept = HandshakeAcceptPacket.parse(responsePacket.data, responsePacket.length)
                if (accept == null) {
                    _telemetry.value = _telemetry.value.copy(
                        state = ReceiverState.ERROR,
                        errorMessage = "Invalid handshake response or session code rejected."
                    )
                    disconnect()
                    return@launch
                }

                sessionToken = accept.sessionToken
                _telemetry.value = _telemetry.value.copy(
                    state = ReceiverState.HANDSHAKE_ACCEPTED,
                    connectedHost = hostIp
                )

                // 3. Initialize AudioTrack for decoded PCM playback
                initAudioTrack(accept.sampleRate, accept.channels)

                // 4. Start Receiver & Playback Loops
                socket?.soTimeout = 1000 // Shorter timeout for audio stream loop
                launch { receiveAudioPackets() }
                launch { playoutJitterBuffer() }
                launch { periodicHeartbeat() }

            } catch (e: Exception) {
                _telemetry.value = _telemetry.value.copy(
                    state = ReceiverState.ERROR,
                    errorMessage = e.message ?: "Failed to connect to host"
                )
                disconnect()
            }
        }
    }

    private fun initAudioTrack(sampleRate: Int, channels: Int) {
        val channelMask = if (channels == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelMask, AudioFormat.ENCODING_PCM_16BIT)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelMask)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
    }

    private suspend fun receiveAudioPackets() {
        val buffer = ByteArray(2048)
        var lastArrivalNs = System.nanoTime()
        var runningJitter = 0.0

        while (isRunning) {
            try {
                val packet = DatagramPacket(buffer, buffer.size)
                socket?.receive(packet)

                val nowNs = System.nanoTime()
                val deltaArrivalMs = (nowNs - lastArrivalNs) / 1_000_000.0
                lastArrivalNs = nowNs
                runningJitter = (runningJitter * 0.95) + (kotlin.math.abs(deltaArrivalMs - 5.0) * 0.05)

                val parsed = AudioPayloadPacket.parse(packet.data, packet.length)
                if (parsed != null && parsed.sessionToken == sessionToken) {
                    // Update state
                    if (_telemetry.value.state != ReceiverState.STREAMING) {
                        _telemetry.value = _telemetry.value.copy(state = ReceiverState.STREAMING)
                    }

                    // Sequence tracking for loss
                    var dropped = _telemetry.value.packetsDropped
                    if (expectedSequence >= 0 && parsed.chunkSequence > expectedSequence) {
                        dropped += (parsed.chunkSequence - expectedSequence)
                    }
                    expectedSequence = parsed.chunkSequence + 1

                    // Insert into jitter buffer
                    jitterBuffer[parsed.chunkSequence] = parsed.payload

                    // Cap buffer size if getting backed up
                    while (jitterBuffer.size > 40) {
                        val firstKey = jitterBuffer.firstKey()
                        jitterBuffer.remove(firstKey)
                    }

                    _telemetry.value = _telemetry.value.copy(
                        packetsReceived = _telemetry.value.packetsReceived + 1,
                        packetsDropped = dropped,
                        bufferFillCount = jitterBuffer.size,
                        jitterMs = (runningJitter * 10).toInt() / 10.0
                    )
                }
            } catch (e: SocketTimeoutException) {
                // Timeout waiting for packet, loop continues
            } catch (e: Exception) {
                if (isRunning) {
                    _telemetry.value = _telemetry.value.copy(
                        state = ReceiverState.ERROR,
                        errorMessage = e.message
                    )
                }
                break
            }
        }
    }

    private suspend fun playoutJitterBuffer() {
        while (isRunning) {
            // Respect user-configured lip-sync delay (e.g. 0 to 45ms)
            if (lipSyncDelayMs > 0 && jitterBuffer.size < (lipSyncDelayMs / 5)) {
                delay(5)
                continue
            }

            if (jitterBuffer.isNotEmpty()) {
                val entry = jitterBuffer.pollFirstEntry()
                if (entry != null) {
                    val pcmData = entry.value
                    audioTrack?.write(pcmData, 0, pcmData.size)
                }
            } else {
                delay(2)
            }
        }
    }

    private suspend fun periodicHeartbeat() {
        while (isRunning) {
            delay(2000)
            try {
                val host = hostAddress ?: continue
                // Send TimeSync Request to measure RTT
                val t1 = System.nanoTime()
                val syncPacket = TimeSyncPacket(
                    sequenceNumber = System.currentTimeMillis(),
                    t1OriginTimestampNs = t1
                ).toBinary()
                val packet = DatagramPacket(syncPacket, syncPacket.size, host, hostPort)
                socket?.send(packet)
            } catch (e: Exception) {
                // Ignore network heartbeat blips
            }
        }
    }

    fun disconnect() {
        isRunning = false
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore
        }
        socket = null

        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null

        jitterBuffer.clear()
        expectedSequence = -1L

        _telemetry.value = _telemetry.value.copy(
            state = ReceiverState.DISCONNECTED
        )
    }
}
