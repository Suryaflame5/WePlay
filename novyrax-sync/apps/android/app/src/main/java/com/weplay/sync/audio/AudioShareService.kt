package com.weplay.sync.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.weplay.sync.MainActivity
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureSourceType
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.SharingTransport
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
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

class AudioShareService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    private var audioRecord: AudioRecord? = null
    private var mediaProjection: MediaProjection? = null
    private var projectionCallback: MediaProjection.Callback? = null

    @Volatile
    private var isRecording = false
    @Volatile
    private var isMuted = false

    // Network distribution socket
    private var broadcastSocket: DatagramSocket? = null
    private var sessionToken: Int = 0

    // Real connected listeners map: key = "ip:port", value = ListenerEndpoint
    private val activeListeners = ConcurrentHashMap<String, ListenerEndpoint>()

    private val _session = MutableStateFlow(createInitialSession())
    val session: StateFlow<AudioSharingSession> = _session.asStateFlow()

    data class ListenerEndpoint(
        val id: String,
        val name: String,
        val address: InetAddress,
        val port: Int,
        val deviceType: DeviceType,
        var lastHeartbeatNs: Long = System.nanoTime(),
        var rttMs: Double = 0.0,
        var clockOffsetMs: Double = 0.0
    )

    inner class LocalBinder : Binder() {
        fun getService(): AudioShareService = this@AudioShareService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createInitialSession(): AudioSharingSession {
        val randomPin = (1000..9999).random()
        val randomCode = "WP-$randomPin"
        sessionToken = SecureRandom().nextInt()

        return AudioSharingSession(
            sessionCode = randomCode,
            sessionName = "My Device Audio",
            isHost = true,
            sourceType = CaptureSourceType.DEVICE_MEDIA_PLAYBACK,
            status = CaptureStatus.READY_TO_START,
            transport = SharingTransport.WIFI_RECEIVER,
            listeners = emptyList(), // Honest: zero listeners until verified connection
            bufferDepthMs = 30,
            sampleRate = 48000,
            bitDepth = 16,
            rttMs = 0.0
        )
    }

    fun startSharingWithResult(resultCode: Int, resultData: Intent) {
        val notification = buildForegroundNotification("WePlay is sharing system audio with connected listeners...")

        // Android 14+ (API 34+) explicit foreground service type declaration MUST precede getMediaProjection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            }
            startForeground(NOTIFICATION_ID, notification, fgsType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Start UDP listener socket for companion pairing
        startNetworkDistributionServer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val projection = mpManager.getMediaProjection(resultCode, resultData)
                if (projection != null) {
                    mediaProjection = projection
                    registerProjectionLifecycle(projection)
                    setupPlaybackCapture(projection)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _session.value = _session.value.copy(status = CaptureStatus.STOPPED)
            }
        } else {
            _session.value = _session.value.copy(
                status = CaptureStatus.CAPTURING_ACTIVE
            )
        }
    }

    fun startSharing(projection: MediaProjection?) {
        val notification = buildForegroundNotification("WePlay is sharing system audio with connected listeners...")

        // Android 14+ (API 34+) explicit foreground service type declaration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            }
            startForeground(NOTIFICATION_ID, notification, fgsType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Start UDP listener socket for companion pairing
        startNetworkDistributionServer()

        if (projection != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaProjection = projection
            registerProjectionLifecycle(projection)
            setupPlaybackCapture(projection)
        } else {
            // Standby mode when MediaProjection is pending
            _session.value = _session.value.copy(
                status = CaptureStatus.CAPTURING_ACTIVE
            )
        }
    }

    private fun registerProjectionLifecycle(projection: MediaProjection) {
        val callback = object : MediaProjection.Callback() {
            override fun onStop() {
                // System notification "Stop sharing" clicked or permission revoked
                stopSharing()
                _session.value = _session.value.copy(
                    status = CaptureStatus.STOPPED
                )
            }
        }
        projectionCallback = callback
        projection.registerCallback(callback, null)
    }

    private fun startNetworkDistributionServer() {
        serviceScope.launch {
            try {
                broadcastSocket = DatagramSocket(HOST_PORT).apply {
                    broadcast = true
                }

                val buffer = ByteArray(2048)
                while (isActive && isRecording) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    broadcastSocket?.receive(packet)

                    handleIncomingPacket(packet)
                }
            } catch (e: Exception) {
                // Socket closed on stop
            }
        }

        // Background listener cleanup loop (prunes dropped companions)
        serviceScope.launch {
            while (isActive) {
                delay(3000)
                pruneInactiveListeners()
            }
        }
    }

    private fun handleIncomingPacket(packet: DatagramPacket) {
        val data = packet.data
        val length = packet.length
        if (length < 3) return

        val typeCode = data[2]
        when (typeCode) {
            PacketType.HANDSHAKE_REQUEST.code -> {
                val req = HandshakeRequestPacket.parse(data, length)
                if (req != null && req.sessionCode.trim().uppercase() == _session.value.sessionCode.trim().uppercase()) {
                    val endpointKey = "${packet.address.hostAddress}:${packet.port}"
                    val devType = if (req.deviceTypeCode == 0.toByte()) DeviceType.HEADPHONES else DeviceType.SPEAKER
                    val endpoint = ListenerEndpoint(
                        id = endpointKey,
                        name = req.listenerName,
                        address = packet.address,
                        port = packet.port,
                        deviceType = devType
                    )
                    activeListeners[endpointKey] = endpoint
                    updateConnectedListenersUi()

                    // Send Accept response
                    val acceptPacket = HandshakeAcceptPacket(sessionToken).toBinary()
                    val resp = DatagramPacket(acceptPacket, acceptPacket.size, packet.address, packet.port)
                    broadcastSocket?.send(resp)
                }
            }
            PacketType.TIME_SYNC_REQUEST.code -> {
                val t2 = System.nanoTime()
                val sync = TimeSyncPacket.parse(data)
                val t3 = System.nanoTime()
                val respSync = TimeSyncPacket(
                    sequenceNumber = sync.sequenceNumber,
                    t1OriginTimestampNs = sync.t1OriginTimestampNs,
                    t2ReceiveTimestampNs = t2,
                    t3TransmitTimestampNs = t3
                ).toBinary()
                val resp = DatagramPacket(respSync, respSync.size, packet.address, packet.port)
                broadcastSocket?.send(resp)

                // Update endpoint timestamp
                val endpointKey = "${packet.address.hostAddress}:${packet.port}"
                activeListeners[endpointKey]?.lastHeartbeatNs = System.nanoTime()
            }
            PacketType.SESSION_CLOSE.code -> {
                val endpointKey = "${packet.address.hostAddress}:${packet.port}"
                activeListeners.remove(endpointKey)
                updateConnectedListenersUi()
            }
        }
    }

    private fun pruneInactiveListeners() {
        val now = System.nanoTime()
        val timeoutNs = 8_000_000_000L // 8 seconds
        var modified = false

        for ((key, endpoint) in activeListeners) {
            if (now - endpoint.lastHeartbeatNs > timeoutNs) {
                activeListeners.remove(key)
                modified = true
            }
        }

        if (modified) {
            updateConnectedListenersUi()
        }
    }

    private fun updateConnectedListenersUi() {
        val list = activeListeners.values.map { endpoint ->
            ConnectedListener(
                id = endpoint.id,
                name = endpoint.name,
                deviceType = endpoint.deviceType,
                transport = SharingTransport.WIFI_RECEIVER,
                signalQuality = 1.0f,
                clockOffsetMs = endpoint.clockOffsetMs,
                rttMs = endpoint.rttMs
            )
        }
        _session.value = _session.value.copy(listeners = list)
    }

    private fun setupPlaybackCapture(projection: MediaProjection) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        try {
            val config = AudioPlaybackCaptureConfiguration.Builder(projection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                .build()

            val sampleRate = 48000
            val channelConfig = AudioFormat.CHANNEL_IN_STEREO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            audioRecord = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setAudioPlaybackCaptureConfig(config)
                .setBufferSizeInBytes(minBufferSize * 4)
                .build()

            audioRecord?.startRecording()
            isRecording = true

            _session.value = _session.value.copy(
                status = CaptureStatus.CAPTURING_ACTIVE
            )

            // Capture and packetization loop: 5ms slices = 240 samples * 4 bytes = 960 bytes
            serviceScope.launch {
                val chunkSize = 960
                val pcmBuffer = ByteArray(chunkSize)
                var chunkSequence = 0L

                while (isActive && isRecording) {
                    val read = audioRecord?.read(pcmBuffer, 0, chunkSize) ?: 0
                    if (read > 0) {
                        val pts = System.nanoTime()

                        // If muted by user in WePlay, silence the payload
                        val payloadToSend = if (isMuted) ByteArray(read) else pcmBuffer

                        val packetData = AudioPayloadPacket(
                            sessionToken = sessionToken,
                            chunkSequence = chunkSequence++,
                            presentationTimestampNs = pts,
                            payload = payloadToSend
                        ).toBinary()

                        // Distribute to all verified active listeners
                        for (listener in activeListeners.values) {
                            try {
                                val udpPacket = DatagramPacket(
                                    packetData,
                                    packetData.size,
                                    listener.address,
                                    listener.port
                                )
                                broadcastSocket?.send(udpPacket)
                            } catch (e: Exception) {
                                // Ignore intermittent UDP send failures
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _session.value = _session.value.copy(
                status = CaptureStatus.STOPPED
            )
        }
    }

    fun stopSharing() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        projectionCallback?.let { mediaProjection?.unregisterCallback(it) }
        mediaProjection?.stop()
        mediaProjection = null

        // Send disconnect packet to listeners
        val closePacket = ByteArray(3).apply {
            this[0] = AudioPayloadPacket.MAGIC_0
            this[1] = AudioPayloadPacket.MAGIC_1
            this[2] = PacketType.SESSION_CLOSE.code
        }
        for (listener in activeListeners.values) {
            try {
                broadcastSocket?.send(DatagramPacket(closePacket, closePacket.size, listener.address, listener.port))
            } catch (e: Exception) {
                // Ignore
            }
        }
        broadcastSocket?.close()
        broadcastSocket = null
        activeListeners.clear()

        _session.value = _session.value.copy(
            status = CaptureStatus.STOPPED,
            listeners = emptyList()
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun toggleMute() {
        isMuted = !isMuted
        val newStatus = if (isMuted) CaptureStatus.MUTED else CaptureStatus.CAPTURING_ACTIVE
        _session.value = _session.value.copy(status = newStatus)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WePlay Audio Sharing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies when WePlay is sharing audio in the background"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WePlay • On Air")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        stopSharing()
        super.onDestroy()
    }

    companion object {
        const val HOST_PORT = 48200
        private const val NOTIFICATION_ID = 4091
        private const val CHANNEL_ID = "weplay_audio_share_channel"
    }
}
