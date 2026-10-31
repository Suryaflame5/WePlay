package com.weplay.sync.shared.model

enum class DeviceType {
    HEADPHONES,
    SPEAKER,
    NETWORK_RECEIVER,
    HI_FI_SYSTEM,
    SMART_TV,
    CAR_AUDIO,
    SYSTEM_BUILTIN
}

enum class TransportType {
    BLUETOOTH_A2DP,
    LE_AUDIO_UNICAST,
    LE_AUDIO_AURACAST,
    WIFI_SYNC,
    WIRED_EXTERNAL,
    BUILTIN_SPEAKER
}

enum class ConnectionStatus {
    AVAILABLE,
    CONNECTING,
    CONNECTED,
    AUDIO_READY,
    PLAYING,
    SYNC_WARNING,
    RECONNECTING,
    UNSUPPORTED
}

data class AudioCapability(
    val supportedSampleRates: List<Int> = listOf(44100, 48000, 96000),
    val supportedBitDepths: List<Int> = listOf(16, 24),
    val supportsVolumeAttenuation: Boolean = true,
    val supportsLatencyMeasurement: Boolean = false,
    val supportsIndependentClockSync: Boolean = false,
    val supportedCodecs: List<String> = listOf("SBC", "AAC", "FLAC", "PCM")
)

data class SyncMetrics(
    val clockOffsetMs: Double = 0.0,
    val roundTripTimeMs: Double = 0.0,
    val jitterMs: Double = 0.0,
    val estimatedDriftMs: Double = 0.0,
    val packetLossPercentage: Double = 0.0,
    val bufferHealth: Double = 1.0 // 0.0 to 1.0
)

data class AudioOutputDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val transport: TransportType,
    val status: ConnectionStatus,
    val volume: Float = 0.8f, // 0.0f to 1.0f
    val isMuted: Boolean = false,
    val isSelectedForPlayback: Boolean = false,
    val capability: AudioCapability = AudioCapability(),
    val syncMetrics: SyncMetrics? = null,
    val hardwareAddress: String? = null,
    val statusMessage: String? = null
)

data class DeviceGroup(
    val id: String,
    val name: String,
    val deviceIds: List<String>,
    val masterVolume: Float = 0.75f,
    val isMuted: Boolean = false
)

enum class AudioQualitySetting {
    SOURCE_PRESERVING, // Bit-perfect pass-through (FLAC/ALAC up to 192/24)
    HIGH_QUALITY,      // High bitrate (320kbps Opus / 24-bit 48kHz)
    LOW_BANDWIDTH      // Resilient (128kbps AAC/Opus)
}

enum class SyncModeSetting {
    TIGHT_ALIGNMENT,   // Dynamic 20ms-80ms buffer adjustment for zero perceived echo
    LOW_LATENCY        // Direct playback with minimal buffering (for video/interactive)
}
