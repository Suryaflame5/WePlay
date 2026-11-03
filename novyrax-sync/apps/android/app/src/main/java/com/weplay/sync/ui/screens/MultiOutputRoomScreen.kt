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
import androidx.compose.ui.graphics.Brush
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
            .background(Color(0xFF0C0F14))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        // 1. Studio Rack Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayMark(size = 28.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MULTI-DESTINATION ROOM",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                        color = Color(0xFF00E5C8)
                    )
                    Text(
                        text = "PTP CLOCK SYNCHRONIZATION",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, color = Color(0xFF777D86))
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF19202A))
                    .border(1.dp, Color(0xFF283442), RoundedCornerShape(4.dp))
                    .clickable { onClose() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CLOSE ✕",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFFF1F5F9)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Master Attenuation Rack Unit
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF131922))
                .border(1.dp, Color(0xFF232D3B), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MASTER BUS ATTENUATION",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "Global analog console reference level",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF777D86)
                    )
                }

                // Stereo Master Peak VU
                TactileVUMeter(
                    level = if (isMasterMuted) 0f else masterVolume,
                    isPlaying = !isMasterMuted,
                    barCount = 14,
                    modifier = Modifier.width(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TactileConsoleFader(
                value = if (isMasterMuted) 0f else masterVolume,
                onValueChange = onMasterVolumeChange,
                label = "MASTER BUS",
                enabled = !isMasterMuted
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Assigned Destinations Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (activeSinksCount > 0) Color(0xFF00E5C8) else Color(0xFFFF9500))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$activeSinksCount ACTIVE SINKS IN CLOCK SYNC",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                    color = Color(0xFFF1F5F9)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF00E5C8))
                    .clickable { onAddDeviceClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "+ ADD HARDWARE",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                    color = Color(0xFF0B1017)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Output Route Cards with Hardware Vector Glyphs & Console Faders
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
