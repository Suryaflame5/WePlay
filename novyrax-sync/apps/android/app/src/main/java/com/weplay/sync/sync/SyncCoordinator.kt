package com.weplay.sync.sync

import com.weplay.sync.shared.model.SyncMetrics
import com.weplay.sync.shared.model.SyncModeSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class SyncCoordinator {
    private val _syncMode = MutableStateFlow(SyncModeSetting.TIGHT_ALIGNMENT)
    val syncMode: StateFlow<SyncModeSetting> = _syncMode.asStateFlow()

    private val _deviceMetrics = MutableStateFlow<Map<String, SyncMetrics>>(emptyMap())
    val deviceMetrics: StateFlow<Map<String, SyncMetrics>> = _deviceMetrics.asStateFlow()

    fun updateMetrics(deviceId: String, rttNs: Long, offsetNs: Long) {
        val rttMs = rttNs.toDouble() / 1_000_000.0
        val offsetMs = offsetNs.toDouble() / 1_000_000.0
        val estimatedDrift = abs(offsetMs)

        val newMetric = SyncMetrics(
            clockOffsetMs = offsetMs,
            roundTripTimeMs = rttMs,
            jitterMs = 1.2,
            estimatedDriftMs = estimatedDrift,
            packetLossPercentage = 0.0,
            bufferHealth = if (estimatedDrift > 15.0) 0.7 else 1.0
        )

        val map = _deviceMetrics.value.toMutableMap()
        map[deviceId] = newMetric
        _deviceMetrics.value = map
    }

    fun setSyncMode(mode: SyncModeSetting) {
        _syncMode.value = mode
    }
}
