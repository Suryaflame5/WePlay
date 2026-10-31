package com.weplay.sync.shared.protocol

import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class PacketType(val code: Byte) {
    TIME_SYNC_REQUEST(0x01),
    TIME_SYNC_RESPONSE(0x02),
    AUDIO_METADATA_HEADER(0x03),
    AUDIO_PAYLOAD_CHUNK(0x04),
    PLAYBACK_COMMAND(0x05),
    RECEIVER_TELEMETRY(0x06),
    HANDSHAKE_REQUEST(0x07),
    HANDSHAKE_ACCEPT(0x08),
    HEARTBEAT(0x09),
    SESSION_CLOSE(0x0A);

    companion object {
        fun fromCode(code: Byte): PacketType? = entries.find { it.code == code }
    }
}

/**
 * 25-byte Standard Audio Packet Header:
 * - magic (2 bytes): 0x5750 ('WP')
 * - type (1 byte): 0x04
 * - sessionToken (4 bytes): CRC32 / truncated hash of session credentials
 * - chunkSequence (8 bytes): monotonic packet counter
 * - presentationTimestampNs (8 bytes): system timeline timestamp
 * - payloadSizeBytes (2 bytes): length of PCM payload (typically 960 bytes for 5ms 48kHz stereo)
 */
data class AudioPayloadPacket(
    val sessionToken: Int,
    val chunkSequence: Long,
    val presentationTimestampNs: Long,
    val payload: ByteArray
) {
    fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(HEADER_SIZE + payload.size).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MAGIC_0)
        buffer.put(MAGIC_1)
        buffer.put(PacketType.AUDIO_PAYLOAD_CHUNK.code)
        buffer.putInt(sessionToken)
        buffer.putLong(chunkSequence)
        buffer.putLong(presentationTimestampNs)
        buffer.putShort(payload.size.toShort())
        buffer.put(payload)
        return buffer.array()
    }

    companion object {
        const val HEADER_SIZE = 25
        const val MAGIC_0: Byte = 0x57 // 'W'
        const val MAGIC_1: Byte = 0x50 // 'P'

        fun parse(data: ByteArray, length: Int): AudioPayloadPacket? {
            if (length < HEADER_SIZE) return null
            val buffer = ByteBuffer.wrap(data, 0, length).order(ByteOrder.BIG_ENDIAN)
            if (buffer.get() != MAGIC_0 || buffer.get() != MAGIC_1) return null
            if (buffer.get() != PacketType.AUDIO_PAYLOAD_CHUNK.code) return null

            val token = buffer.int
            val seq = buffer.long
            val pts = buffer.long
            val payloadSize = buffer.short.toInt() and 0xFFFF

            if (length < HEADER_SIZE + payloadSize) return null
            val payload = ByteArray(payloadSize)
            buffer.get(payload)

            return AudioPayloadPacket(token, seq, pts, payload)
        }
    }
}

/**
 * Handshake Request Packet: sent by listener to host over UDP
 */
data class HandshakeRequestPacket(
    val sessionCode: String,     // e.g. "WP-4829"
    val listenerName: String,    // e.g. "Robin's Buds"
    val deviceTypeCode: Byte     // 0=Headphones, 1=Speaker, 2=Phone
) {
    fun toBinary(): ByteArray {
        val codeBytes = sessionCode.toByteArray(Charsets.UTF_8)
        val nameBytes = listenerName.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(7 + codeBytes.size + nameBytes.size).order(ByteOrder.BIG_ENDIAN)
        buffer.put(AudioPayloadPacket.MAGIC_0)
        buffer.put(AudioPayloadPacket.MAGIC_1)
        buffer.put(PacketType.HANDSHAKE_REQUEST.code)
        buffer.put(deviceTypeCode)
        buffer.put(codeBytes.size.toByte())
        buffer.put(codeBytes)
        buffer.put(nameBytes.size.toByte())
        buffer.put(nameBytes)
        return buffer.array()
    }

    companion object {
        fun parse(data: ByteArray, length: Int): HandshakeRequestPacket? {
            if (length < 7) return null
            val buffer = ByteBuffer.wrap(data, 0, length).order(ByteOrder.BIG_ENDIAN)
            if (buffer.get() != AudioPayloadPacket.MAGIC_0 || buffer.get() != AudioPayloadPacket.MAGIC_1) return null
            if (buffer.get() != PacketType.HANDSHAKE_REQUEST.code) return null

            val devType = buffer.get()
            val codeLen = buffer.get().toInt() and 0xFF
            if (buffer.remaining() < codeLen) return null
            val codeBytes = ByteArray(codeLen)
            buffer.get(codeBytes)

            val nameLen = buffer.get().toInt() and 0xFF
            if (buffer.remaining() < nameLen) return null
            val nameBytes = ByteArray(nameLen)
            buffer.get(nameBytes)

            return HandshakeRequestPacket(
                sessionCode = String(codeBytes, Charsets.UTF_8),
                listenerName = String(nameBytes, Charsets.UTF_8),
                deviceTypeCode = devType
            )
        }
    }
}

/**
 * Handshake Accept Packet: host sends back session token, negotiated sample rate & channels
 */
data class HandshakeAcceptPacket(
    val sessionToken: Int,
    val sampleRate: Int = 48000,
    val channels: Int = 2,
    val bitDepth: Int = 16,
    val bufferTargetMs: Int = 30
) {
    fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(19).order(ByteOrder.BIG_ENDIAN)
        buffer.put(AudioPayloadPacket.MAGIC_0)
        buffer.put(AudioPayloadPacket.MAGIC_1)
        buffer.put(PacketType.HANDSHAKE_ACCEPT.code)
        buffer.putInt(sessionToken)
        buffer.putInt(sampleRate)
        buffer.put(channels.toByte())
        buffer.put(bitDepth.toByte())
        buffer.putShort(bufferTargetMs.toShort())
        return buffer.array()
    }

    companion object {
        fun parse(data: ByteArray, length: Int): HandshakeAcceptPacket? {
            if (length < 17) return null
            val buffer = ByteBuffer.wrap(data, 0, length).order(ByteOrder.BIG_ENDIAN)
            if (buffer.get() != AudioPayloadPacket.MAGIC_0 || buffer.get() != AudioPayloadPacket.MAGIC_1) return null
            if (buffer.get() != PacketType.HANDSHAKE_ACCEPT.code) return null

            val token = buffer.int
            val sampleRate = buffer.int
            val channels = buffer.get().toInt() and 0xFF
            val bitDepth = buffer.get().toInt() and 0xFF
            val bufferTarget = buffer.short.toInt() and 0xFFFF
            return HandshakeAcceptPacket(token, sampleRate, channels, bitDepth, bufferTarget)
        }
    }
}

data class TimeSyncPacket(
    val sequenceNumber: Long,
    val t1OriginTimestampNs: Long,
    val t2ReceiveTimestampNs: Long = 0L,
    val t3TransmitTimestampNs: Long = 0L
) {
    fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(33).order(ByteOrder.BIG_ENDIAN)
        buffer.put(PacketType.TIME_SYNC_REQUEST.code)
        buffer.putLong(sequenceNumber)
        buffer.putLong(t1OriginTimestampNs)
        buffer.putLong(t2ReceiveTimestampNs)
        buffer.putLong(t3TransmitTimestampNs)
        return buffer.array()
    }

    companion object {
        fun parse(data: ByteArray): TimeSyncPacket {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
            val typeCode = buffer.get()
            val seq = buffer.long
            val t1 = buffer.long
            val t2 = buffer.long
            val t3 = buffer.long
            return TimeSyncPacket(seq, t1, t2, t3)
        }
    }
}
