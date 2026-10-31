package com.weplay.sync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun OutputRouteCard(
    device: AudioOutputDevice,
    onToggleSelect: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isInteractive = device.status != ConnectionStatus.UNSUPPORTED
    val isSelected = device.isSelectedForPlayback

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(WePlayTheme.colors.surface)
            .border(
                width = 1.dp,
                color = when {
                    device.status == ConnectionStatus.PLAYING && isSelected -> WePlayTheme.colors.accent
                    device.status == ConnectionStatus.SYNC_WARNING -> WePlayTheme.colors.warning
                    device.status == ConnectionStatus.UNSUPPORTED -> WePlayTheme.colors.separator.copy(alpha = 0.5f)
                    isSelected -> WePlayTheme.colors.textPrimary
                    else -> WePlayTheme.colors.separator
                },
                shape = RoundedCornerShape(4.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equipment Glyph Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(WePlayTheme.colors.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (device.type) {
                        DeviceType.HEADPHONES -> "🎧"
                        DeviceType.SPEAKER -> "🔊"
                        DeviceType.HI_FI_SYSTEM -> "📻"
                        DeviceType.NETWORK_RECEIVER -> "📡"
                        DeviceType.CAR_AUDIO -> "🚗"
                        DeviceType.SMART_TV -> "📺"
                        DeviceType.SYSTEM_BUILTIN -> "📱"
                    },
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Device Title & Transport Telemetry
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = WePlayTheme.typography.titleMedium,
                    color = if (isInteractive) WePlayTheme.colors.textPrimary else WePlayTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (device.transport) {
                        TransportType.WIFI_SYNC -> "WePlay Wi-Fi Sync • 24-bit LPCM"
                        TransportType.BLUETOOTH_A2DP -> "Classic Bluetooth A2DP • Host Stack"
                        TransportType.LE_AUDIO_UNICAST -> "Bluetooth LE Audio Unicast"
                        TransportType.LE_AUDIO_AURACAST -> "Auracast Broadcast Assistant (LC3)"
                        TransportType.WIRED_EXTERNAL -> "USB-C Audio / External DAC"
                        TransportType.BUILTIN_SPEAKER -> "Internal Device Speaker"
                    },
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }

            // Route Binding Selector
            if (isInteractive) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) WePlayTheme.colors.accent else WePlayTheme.colors.separator,
                            shape = CircleShape
                        )
                        .background(if (isSelected) WePlayTheme.colors.accent else Color.Transparent)
                        .clickable { onToggleSelect(device.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(WePlayTheme.colors.canvas)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status & Synchronization Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (statusText, statusColor) = when (device.status) {
                ConnectionStatus.AVAILABLE -> Pair("AVAILABLE", WePlayTheme.colors.textSecondary)
                ConnectionStatus.CONNECTING -> Pair("CONNECTING...", WePlayTheme.colors.accent)
                ConnectionStatus.CONNECTED -> Pair("CONNECTED", WePlayTheme.colors.textPrimary)
                ConnectionStatus.AUDIO_READY -> Pair("AUDIO READY", WePlayTheme.colors.accent)
                ConnectionStatus.PLAYING -> Pair("STREAMING", WePlayTheme.colors.accent)
                ConnectionStatus.SYNC_WARNING -> Pair("SYNC WARNING", WePlayTheme.colors.warning)
                ConnectionStatus.RECONNECTING -> Pair("RECONNECTING", WePlayTheme.colors.warning)
                ConnectionStatus.UNSUPPORTED -> Pair("UNSUPPORTED ROUTE", WePlayTheme.colors.textSecondary)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = statusText,
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (device.syncMetrics != null && isSelected) {
                val drift = device.syncMetrics.estimatedDriftMs
                Text(
                    text = "${if (drift >= 0) "+$drift" else "$drift"}ms drift • ${device.syncMetrics.roundTripTimeMs.toInt()}ms RTT",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = if (drift > 15.0) WePlayTheme.colors.warning else WePlayTheme.colors.textSecondary
                )
            } else if (device.transport == TransportType.BLUETOOTH_A2DP) {
                Text(
                    text = "Single route only",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
        }

        // Dedicated Volume Attenuator (Only offered when hardware route permits it)
        if (isSelected && isInteractive) {
            Spacer(modifier = Modifier.height(10.dp))
            HiFiVolumeSlider(
                value = device.volume,
                onValueChange = { onVolumeChange(device.id, it) },
                enabled = device.capability.supportsVolumeAttenuation,
                unsupportedReason = if (!device.capability.supportsVolumeAttenuation) "Fixed line-out • Direct hardware attenuation" else null
            )
        }

        // Explicit reason if unsupported
        if (device.status == ConnectionStatus.UNSUPPORTED && device.statusMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = device.statusMessage,
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }
    }
}
