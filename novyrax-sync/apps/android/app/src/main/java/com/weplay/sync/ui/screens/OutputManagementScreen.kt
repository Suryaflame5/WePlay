package com.weplay.sync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.weplay.sync.ui.components.HiFiVolumeSlider
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun OutputManagementScreen(
    outputs: List<AudioOutputDevice>,
    masterVolume: Float,
    isMasterMuted: Boolean,
    onMasterVolumeChange: (Float) -> Unit,
    onMasterMuteToggle: () -> Unit,
    onDeviceVolumeChange: (String, Float) -> Unit,
    onToggleDevice: (String) -> Unit,
    onScanDevices: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCount = outputs.count { it.isSelectedForPlayback }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayMark(size = 24.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OUTPUT MANAGEMENT",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Text(
                    text = "CLOSE ✕",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Audio Destinations",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp),
            color = WePlayTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$activeCount active outputs receiving broadcast",
            style = WePlayTheme.typography.bodyMedium,
            color = WePlayTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Master Attenuation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MASTER ATTENUATION",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                        Text(
                            text = "Global analog-modeled output level",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(WePlayTheme.colors.surfaceSubtle)
                            .clickable { onMasterMuteToggle() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isMasterMuted) "UNMUTE ALL" else "MUTE ALL",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${(masterVolume * 100).toInt()}%",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 13.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                HiFiVolumeSlider(
                    value = masterVolume,
                    onValueChange = onMasterVolumeChange,
                    enabled = !isMasterMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Outputs List Header with Scan Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ASSIGNED DESTINATIONS",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = WePlayTheme.colors.textSecondary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(WePlayTheme.colors.textPrimary)
                    .clickable { onScanDevices() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+ SCAN SENSORS",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = WePlayTheme.colors.canvas
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Device Route Cards
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(outputs) { device ->
                OutputRouteCard(
                    device = device,
                    onToggleSelect = onToggleDevice,
                    onVolumeChange = onDeviceVolumeChange
                )
            }
        }
    }
}
