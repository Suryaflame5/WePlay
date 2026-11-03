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
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.components.TactileConsoleFader
import com.weplay.sync.ui.components.TactileVUMeter
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
            .background(Color(0xFF0A0D12))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                    text = "OUTPUT ROUTING STUDIO",
                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 9.sp),
                    color = Color(0xFF64748B)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF131922))
                    .border(1.dp, Color(0xFF263345), RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "CLOSE ✕",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Audio Destinations",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 28.sp, letterSpacing = (-0.6).sp),
            color = Color(0xFFF8FAFC)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$activeCount active endpoints receiving synchronized stream",
            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = Color(0xFF00E5C8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Master Attenuation Studio Chassis
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF111722))
                .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(10.dp))
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
                            text = "MASTER CONSOLE ATTENUATION",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Global analog-modeled output bus",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isMasterMuted) Color(0xFFFF3B30).copy(alpha = 0.15f) else Color(0xFF192230))
                            .border(1.dp, if (isMasterMuted) Color(0xFFFF3B30) else Color(0xFF263345), RoundedCornerShape(4.dp))
                            .clickable { onMasterMuteToggle() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isMasterMuted) "UNMUTE ALL" else "MUTE ALL",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isMasterMuted) Color(0xFFFF3B30) else Color(0xFFF1F5F9)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TactileVUMeter(level = if (isMasterMuted) 0f else masterVolume, isPlaying = !isMasterMuted)

                Spacer(modifier = Modifier.height(10.dp))

                TactileConsoleFader(
                    value = masterVolume,
                    onValueChange = onMasterVolumeChange,
                    enabled = !isMasterMuted,
                    label = "GANGED MASTER BUS LEVEL"
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
                text = "DISCOVERED DESTINATIONS",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 9.sp),
                color = Color(0xFF64748B)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF003830))
                    .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(4.dp))
                    .clickable { onScanDevices() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+ SCAN SENSORS",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF00E5C8)
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
