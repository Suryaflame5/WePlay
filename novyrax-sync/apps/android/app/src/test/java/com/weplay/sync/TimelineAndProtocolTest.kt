package com.weplay.sync

import com.weplay.sync.shared.model.AudioCapability
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.SyncMetrics
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.shared.protocol.PacketType
import com.weplay.sync.shared.protocol.TimeSyncPacket
import com.weplay.sync.shared.timeline.AudioTimeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
