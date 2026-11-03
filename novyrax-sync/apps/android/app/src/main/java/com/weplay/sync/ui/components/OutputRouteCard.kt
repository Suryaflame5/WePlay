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
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF111722))
            .border(
                width = 1.dp,
                color = when {
                    device.status == ConnectionStatus.PLAYING && isSelected -> Color(0xFF00E5C8)
                    device.status == ConnectionStatus.SYNC_WARNING -> Color(0xFFFF9500)
                    device.status == ConnectionStatus.UNSUPPORTED -> Color(0xFF1E2838)
                    isSelected -> Color(0xFF38BDF8)
                    else -> Color(0xFF1E2838)
                },
                shape = RoundedCornerShape(8.dp)
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF192230)),
                contentAlignment = Alignment.Center
            ) {
                HardwareVectorGlyph(
                    type = device.type,
                    tint = if (isSelected) Color(0xFF00E5C8) else Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Device Title & Transport Telemetry
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = if (isInteractive) Color(0xFFF1F5F9) else Color(0xFF64748B)
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
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    color = Color(0xFF64748B)
                )
            }

            // Route Binding Selector (Tactile Ring)
            if (isInteractive) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) Color(0xFF00E5C8) else Color(0xFF475569),
                            shape = CircleShape
                        )
                        .background(if (isSelected) Color(0xFF003830) else Color.Transparent)
                        .clickable { onToggleSelect(device.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E5C8))
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
                ConnectionStatus.AVAILABLE -> Pair("AVAILABLE", Color(0xFF64748B))
                ConnectionStatus.CONNECTING -> Pair("CONNECTING...", Color(0xFF38BDF8))
                ConnectionStatus.CONNECTED -> Pair("CONNECTED", Color(0xFFCBD5E1))
                ConnectionStatus.AUDIO_READY -> Pair("AUDIO READY", Color(0xFF00E5C8))
                ConnectionStatus.PLAYING -> Pair("STREAMING", Color(0xFF00E5C8))
                ConnectionStatus.SYNC_WARNING -> Pair("SYNC WARNING", Color(0xFFFF9500))
                ConnectionStatus.RECONNECTING -> Pair("RECONNECTING", Color(0xFFFF9500))
                ConnectionStatus.UNSUPPORTED -> Pair("UNSUPPORTED ROUTE", Color(0xFF475569))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = statusText,
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (device.syncMetrics != null && isSelected) {
                val drift = device.syncMetrics.estimatedDriftMs
                Text(
                    text = "${if (drift >= 0) "+$drift" else "$drift"}ms drift • ${device.syncMetrics.roundTripTimeMs.toInt()}ms RTT",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = if (drift > 15.0) Color(0xFFFF9500) else Color(0xFF94A3B8)
                )
            } else if (device.transport == TransportType.BLUETOOTH_A2DP) {
                Text(
                    text = "Single route only",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = Color(0xFF64748B)
                )
            }
        }

        // Dedicated Volume Attenuator (Only offered when hardware route permits it)
        if (isSelected && isInteractive) {
            Spacer(modifier = Modifier.height(10.dp))
            TactileConsoleFader(
                value = device.volume,
                onValueChange = { onVolumeChange(device.id, it) },
                enabled = device.capability.supportsVolumeAttenuation,
                label = "ENDPOINT ATTENUATION"
            )
        }

        // Explicit reason if unsupported
        if (device.status == ConnectionStatus.UNSUPPORTED && device.statusMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = device.statusMessage,
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = Color(0xFF94A3B8)
            )
        }
    }
}
