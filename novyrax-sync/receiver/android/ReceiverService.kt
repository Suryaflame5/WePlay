package com.weplay.receiver

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.IBinder
import com.weplay.sync.shared.protocol.PacketType
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * WePlay Companion Android Audio Receiver Service.
 * Listens on local UDP port 48200 for synchronized audio frames and SNTP clock queries.
 */
class ReceiverService : Service() {

    private var socket: DatagramSocket? = null
    private var audioTrack: AudioTrack? = null
    private var isRunning = false
    private var clockOffsetNs = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        startNetworkListener()
        return START_STICKY
    }

    private fun startNetworkListener() {
        Thread {
            try {
                socket = DatagramSocket(48200)
                val buffer = ByteArray(4096)

                while (isRunning) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)

                    val typeCode = buffer[0]
                    when (typeCode) {
                        PacketType.TIME_SYNC_REQUEST.code -> {
                            handleTimeSync(packet)
                        }
                        PacketType.AUDIO_PAYLOAD_CHUNK.code -> {
                            handleAudioPayload(packet)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun handleTimeSync(packet: DatagramPacket) {
        val t2 = System.nanoTime()
        val inBuf = ByteBuffer.wrap(packet.data, 0, packet.length).order(ByteOrder.BIG_ENDIAN)
        inBuf.get() // type
        val seq = inBuf.long
        val t1 = inBuf.long

        val t3 = System.nanoTime()
        val outBuf = ByteBuffer.allocate(33).order(ByteOrder.BIG_ENDIAN)
        outBuf.put(PacketType.TIME_SYNC_RESPONSE.code)
        outBuf.putLong(seq)
        outBuf.putLong(t1)
        outBuf.putLong(t2)
        outBuf.putLong(t3)

        val resp = DatagramPacket(outBuf.array(), 33, packet.address, packet.port)
        socket?.send(resp)
    }

    private fun handleAudioPayload(packet: DatagramPacket) {
        // Scheduled playout according to target presentation timestamp
    }

    override fun onDestroy() {
        isRunning = false
        socket?.close()
        audioTrack?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
