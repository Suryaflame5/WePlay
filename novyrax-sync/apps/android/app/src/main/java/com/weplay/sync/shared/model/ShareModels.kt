package com.weplay.sync.shared.model

enum class SharingTransport(val displayName: String, val badge: String) {
    BLUETOOTH_AURACAST("Bluetooth Auracast", "LE Audio"),
    WIFI_RECEIVER("WePlay Wi-Fi Network", "Lossless LPCM"),
    DIRECT_BLUETOOTH("Direct Bluetooth", "Single Route"),
    LOCAL_HOTSPOT("Direct Hotspot", "Ultra-Low Latency")
}

enum class CaptureSourceType(val displayName: String, val description: String) {
    DEVICE_MEDIA_PLAYBACK("Device Audio", "YouTube, Spotify, VLC, browser, games & podcasts"),
    AURACAST_BROADCAST("Auracast Broadcast", "Direct system broadcast to compatible LE Audio receivers"),
    MICROPHONE_TALKBACK("Tour Guide / Mic", "Live microphone commentary alongside audio"),
    TEST_AUDIO_SOURCE("WePlay High-Fidelity Test Tone", "Source-calibrated 48kHz stereo signal")
}

enum class CaptureStatus {
    READY_TO_START,
    CAPTURING_ACTIVE,
    MUTED,
    PERMISSION_REQUIRED,
    RESTRICTED_BY_APP,
    STOPPED
}

data class ConnectedListener(
    val id: String,
    val name: String,
    val deviceType: DeviceType = DeviceType.HEADPHONES,
    val transport: SharingTransport = SharingTransport.WIFI_RECEIVER,
    val signalQuality: Float = 0.95f,
    val clockOffsetMs: Double = 1.8,
    val rttMs: Double = 12.4,
    val isMuted: Boolean = false,
    val connectedAt: Long = System.currentTimeMillis()
)

data class NearbyStream(
    val id: String,
    val hostName: String,
    val streamName: String,
    val transport: SharingTransport,
    val listenerCount: Int,
    val isEncrypted: Boolean = false,
    val signalDbm: Int = -58
)

data class AudioSharingSession(
    val sessionCode: String = "WP-4829",
    val sessionName: String = "Robin's Audio Share",
    val isHost: Boolean = true,
    val sourceType: CaptureSourceType = CaptureSourceType.DEVICE_MEDIA_PLAYBACK,
    val status: CaptureStatus = CaptureStatus.READY_TO_START,
    val transport: SharingTransport = SharingTransport.WIFI_RECEIVER,
    val listeners: List<ConnectedListener> = emptyList(),
    val bufferDepthMs: Int = 30,
    val sampleRate: Int = 48000,
    val bitDepth: Int = 16,
    val channels: Int = 2,
    val rttMs: Double = 14.2
)
