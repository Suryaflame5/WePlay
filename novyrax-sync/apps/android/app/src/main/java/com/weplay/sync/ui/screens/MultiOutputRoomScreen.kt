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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.HiFiVolumeSlider
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun MultiOutputRoomScreen(
    activeOutputs: List<AudioOutputDevice>,
    masterVolume: Float,
    isMasterMuted: Boolean,
    onMasterVolumeChange: (Float) -> Unit,
    onMasterMuteToggle: () -> Unit,
    onDeviceVolumeChange: (String, Float) -> Unit,
    onToggleDevice: (String) -> Unit,
    onAddDeviceClick: () -> Unit,
    onClose: () -> Unit
) {
    val activeSinksCount = activeOutputs.count { it.isSelectedForPlayback }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayMark(size = 24.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUDIO ROOM",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .clickable { onClose() }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Text(
                    text = "CLOSE ✕",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Atelier Title & Synchronized Topology Summary
        Text(
            text = "Active Audio Room",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
            color = WePlayTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "$activeSinksCount destinations bound to shared master clock",
            style = WePlayTheme.typography.bodyMedium,
            color = WePlayTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Master Attenuation Module (Hi-Fi physical console inspired)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MASTER ATTENUATION",
                        style = WePlayTheme.typography.labelSmall.copy(color = WePlayTheme.colors.textPrimary)
                    )
                    Text(
                        text = "Global analog-modeled output level",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isMasterMuted) WePlayTheme.colors.warning.copy(alpha = 0.15f) else WePlayTheme.colors.surfaceSubtle)
                        .clickable { onMasterMuteToggle() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isMasterMuted) "UNMUTE" else "MUTE ALL",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isMasterMuted) WePlayTheme.colors.warning else WePlayTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HiFiVolumeSlider(
                value = if (isMasterMuted) 0f else masterVolume,
                onValueChange = onMasterVolumeChange,
                enabled = !isMasterMuted
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Destination Sinks Header with + Add Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ASSIGNED DESTINATIONS",
                style = WePlayTheme.typography.labelSmall,
                color = WePlayTheme.colors.textSecondary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(WePlayTheme.colors.textPrimary)
                    .clickable { onAddDeviceClick() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+ ADD OUTPUT",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.canvas
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Individual Destination Cards
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activeOutputs, key = { it.id }) { device ->
                OutputRouteCard(
                    device = device,
                    onToggleSelect = onToggleDevice,
                    onVolumeChange = onDeviceVolumeChange
                )
            }
        }
    }
}
