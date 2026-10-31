package com.weplay.sync.shared.protocol

import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class PacketType(val code: Byte) {
    TIME_SYNC_REQUEST(0x01),
    TIME_SYNC_RESPONSE(0x02),
    AUDIO_METADATA_HEADER(0x03),
    AUDIO_PAYLOAD_CHUNK(0x04),
    PLAYBACK_COMMAND(0x05),
    RECEIVER_TELEMETRY(0x06);

    companion object {
        fun fromCode(code: Byte): PacketType? = entries.find { it.code == code }
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

data class AudioChunkHeader(
    val streamId: Int,
    val chunkSequence: Long,
    val scheduledPresentationTimeNs: Long,
    val sampleRate: Int,
    val channels: Int,
    val bitDepth: Int,
    val payloadSizeBytes: Int
)
