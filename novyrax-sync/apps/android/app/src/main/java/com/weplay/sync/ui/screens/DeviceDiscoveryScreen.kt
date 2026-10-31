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
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun DeviceDiscoveryScreen(
    devices: List<AudioOutputDevice>,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onToggleSelect: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "HARDWARE DISCOVERY",
                style = WePlayTheme.typography.labelSmall,
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title and Scanning Trigger Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nearby Audio Sinks",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isScanning) "Scanning via Bluetooth LE & local mDNS..." else "${devices.size} endpoints discovered",
                    style = WePlayTheme.typography.bodyMedium,
                    color = WePlayTheme.colors.textSecondary
                )
            }

            // Tactile Scanning Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isScanning) WePlayTheme.colors.accent.copy(alpha = 0.15f) else WePlayTheme.colors.surface)
                    .border(1.dp, if (isScanning) WePlayTheme.colors.accent else WePlayTheme.colors.separator, RoundedCornerShape(3.dp))
                    .clickable { if (isScanning) onStopScan() else onStartScan() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(WePlayTheme.colors.accent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (isScanning) "STOP SCAN" else "START SCAN",
                        style = WePlayTheme.typography.labelSmall,
                        color = if (isScanning) WePlayTheme.colors.accent else WePlayTheme.colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Capability Notice (Honest routing explanation)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(WePlayTheme.colors.surfaceSubtle)
                .padding(12.dp)
        ) {
            Text(
                text = "Classic Bluetooth routes audio to one primary sink. WePlay Wi-Fi companions and LE Audio Auracast broadcast support synchronized multi-device output.",
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Devices List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(devices, key = { it.id }) { device ->
                OutputRouteCard(
                    device = device,
                    onToggleSelect = onToggleSelect,
                    onVolumeChange = onVolumeChange
                )
            }
        }
    }
}
