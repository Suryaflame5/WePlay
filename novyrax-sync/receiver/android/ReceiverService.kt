package com.weplay.receiver

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.IBinder
import com.weplay.sync.shared.protocol.AudioPayloadPacket
import com.weplay.sync.shared.protocol.HandshakeAcceptPacket
import com.weplay.sync.shared.protocol.HandshakeRequestPacket
import com.weplay.sync.shared.protocol.PacketType
import com.weplay.sync.shared.protocol.TimeSyncPacket
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.ConcurrentSkipListMap

/**
 * WePlay Companion Android Audio Receiver Standalone Service.
 * Implements real AudioTrack output for companion phones receiving synchronized streams.
 */
class ReceiverService : Service() {

    private var socket: DatagramSocket? = null
    private var audioTrack: AudioTrack? = null
    @Volatile
    private var isRunning = false
    private var sessionToken: Int = 0

    private val jitterBuffer = ConcurrentSkipListMap<Long, ByteArray>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val hostIp = intent?.getStringExtra("EXTRA_HOST_IP") ?: "127.0.0.1"
        val sessionCode = intent?.getStringExtra("EXTRA_SESSION_CODE") ?: "WP-0000"
        isRunning = true
        startReceiverLoop(hostIp, sessionCode)
        return START_NOT_STICKY
    }

    private fun startReceiverLoop(hostIp: String, sessionCode: String) {
        Thread {
            try {
                val host = InetAddress.getByName(hostIp)
                socket = DatagramSocket()

                // Send Handshake
                val req = HandshakeRequestPacket(sessionCode, android.os.Build.MODEL, 0).toBinary()
                socket?.send(DatagramPacket(req, req.size, host, 48200))

                val buf = ByteArray(2048)
                val resp = DatagramPacket(buf, buf.size)
                socket?.receive(resp)

                val accept = HandshakeAcceptPacket.parse(resp.data, resp.length)
                if (accept != null) {
                    sessionToken = accept.sessionToken
                    initAudioTrack(accept.sampleRate, accept.channels)

                    // Start playback thread
                    Thread { playoutLoop() }.start()

                    while (isRunning) {
                        val packet = DatagramPacket(buf, buf.size)
                        socket?.receive(packet)

                        val parsed = AudioPayloadPacket.parse(packet.data, packet.length)
                        if (parsed != null && parsed.sessionToken == sessionToken) {
                            jitterBuffer[parsed.chunkSequence] = parsed.payload
                            while (jitterBuffer.size > 30) {
                                jitterBuffer.pollFirstEntry()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun initAudioTrack(sampleRate: Int, channels: Int) {
        val channelMask = if (channels == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
        val minBuf = AudioTrack.getMinBufferSize(sampleRate, channelMask, AudioFormat.ENCODING_PCM_16BIT)

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
            .setBufferSizeInBytes(minBuf * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
    }

    private fun playoutLoop() {
        while (isRunning) {
            val entry = jitterBuffer.pollFirstEntry()
            if (entry != null) {
                val data = entry.value
                audioTrack?.write(data, 0, data.size)
            } else {
                try {
                    Thread.sleep(2)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        isRunning = false
        socket?.close()
        audioTrack?.stop()
        audioTrack?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
