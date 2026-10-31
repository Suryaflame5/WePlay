package com.weplay.sync

import com.weplay.sync.shared.model.AudioCapability
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.SyncMetrics
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.shared.protocol.AudioPayloadPacket
import com.weplay.sync.shared.protocol.HandshakeAcceptPacket
import com.weplay.sync.shared.protocol.HandshakeRequestPacket
import com.weplay.sync.shared.protocol.PacketType
import com.weplay.sync.shared.protocol.TimeSyncPacket
import com.weplay.sync.shared.timeline.AudioTimeline
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TimelineAndProtocolTest {

    @Test
    fun testAudioTimelineFrameConversion() {
        val timeline = AudioTimeline(referenceSampleRate = 48000)
        // 48000 frames at 48kHz = 1000ms
        assertEquals(1000L, timeline.frameToDurationMs(48000L))
        assertEquals(24000L, timeline.msToFrame(500L))
    }

    @Test
    fun testTimeSyncPacketBinarySerialization() {
        val original = TimeSyncPacket(
            sequenceNumber = 42L,
            t1OriginTimestampNs = 1_000_000_000L,
            t2ReceiveTimestampNs = 1_010_000_000L,
            t3TransmitTimestampNs = 1_012_000_000L
        )

        val binary = original.toBinary()
        assertEquals(33, binary.size)
        assertEquals(PacketType.TIME_SYNC_REQUEST.code, binary[0])

        val parsed = TimeSyncPacket.parse(binary)
        assertEquals(original.sequenceNumber, parsed.sequenceNumber)
        assertEquals(original.t1OriginTimestampNs, parsed.t1OriginTimestampNs)
        assertEquals(original.t2ReceiveTimestampNs, parsed.t2ReceiveTimestampNs)
        assertEquals(original.t3TransmitTimestampNs, parsed.t3TransmitTimestampNs)
    }

    @Test
    fun testAudioPayloadPacketBinarySerialization() {
        val samplePcm = ByteArray(960) { it.toByte() }
        val original = AudioPayloadPacket(
            sessionToken = 0x1A2B3C4D,
            chunkSequence = 1001L,
            presentationTimestampNs = 16_000_000_000L,
            payload = samplePcm
        )

        val binary = original.toBinary()
        assertEquals(AudioPayloadPacket.HEADER_SIZE + samplePcm.size, binary.size)
        assertEquals(AudioPayloadPacket.MAGIC_0, binary[0])
        assertEquals(AudioPayloadPacket.MAGIC_1, binary[1])
        assertEquals(PacketType.AUDIO_PAYLOAD_CHUNK.code, binary[2])

        val parsed = AudioPayloadPacket.parse(binary, binary.size)
        assertNotNull(parsed)
        assertEquals(original.sessionToken, parsed!!.sessionToken)
        assertEquals(original.chunkSequence, parsed.chunkSequence)
        assertEquals(original.presentationTimestampNs, parsed.presentationTimestampNs)
        assertArrayEquals(samplePcm, parsed.payload)
    }

    @Test
    fun testHandshakeNegotiationBinarySerialization() {
        val req = HandshakeRequestPacket(
            sessionCode = "WP-7429",
            listenerName = "Robin's Buds",
            deviceTypeCode = 0
        )
        val reqBinary = req.toBinary()
        val parsedReq = HandshakeRequestPacket.parse(reqBinary, reqBinary.size)
        assertNotNull(parsedReq)
        assertEquals("WP-7429", parsedReq!!.sessionCode)
        assertEquals("Robin's Buds", parsedReq.listenerName)

        val accept = HandshakeAcceptPacket(
            sessionToken = 0x55AA55AA,
            sampleRate = 48000,
            channels = 2,
            bitDepth = 16,
            bufferTargetMs = 30
        )
        val acceptBinary = accept.toBinary()
        val parsedAccept = HandshakeAcceptPacket.parse(acceptBinary, acceptBinary.size)
        assertNotNull(parsedAccept)
        assertEquals(0x55AA55AA, parsedAccept!!.sessionToken)
        assertEquals(48000, parsedAccept.sampleRate)
        assertEquals(2, parsedAccept.channels)
        assertEquals(16, parsedAccept.bitDepth)
    }

    @Test
    fun testDeviceCapabilityAndVolumeAttenuation() {
        val a2dpHeadphones = AudioOutputDevice(
            id = "wh1000xm5",
            name = "WH-1000XM5",
            type = DeviceType.HEADPHONES,
            transport = TransportType.BLUETOOTH_A2DP,
            status = ConnectionStatus.CONNECTED,
            volume = 0.8f,
            capability = AudioCapability(
                supportsVolumeAttenuation = true,
                supportsLatencyMeasurement = false
            )
        )

        assertTrue(a2dpHeadphones.capability.supportsVolumeAttenuation)
        assertFalse(a2dpHeadphones.capability.supportsLatencyMeasurement)
    }

    @Test
    fun testSyncMetricsDriftThreshold() {
        val metric = SyncMetrics(
            clockOffsetMs = -18.4,
            roundTripTimeMs = 32.0,
            jitterMs = 2.4,
            estimatedDriftMs = 18.4,
            packetLossPercentage = 0.5,
            bufferHealth = 0.65
        )

        assertTrue("High drift should exceed warning threshold", metric.estimatedDriftMs > 15.0)
        assertTrue("Buffer health degraded under high drift", metric.bufferHealth < 0.8)
    }
}
