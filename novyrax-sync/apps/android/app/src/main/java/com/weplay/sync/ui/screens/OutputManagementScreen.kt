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
import com.weplay.sync.ui.components.InstrumentChassisCard
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.components.TactileConsoleFader
import com.weplay.sync.ui.components.TactileVUMeter
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.AtelierChamfer
import com.weplay.sync.ui.theme.AtelierGunmetal
import com.weplay.sync.ui.theme.AtelierObsidian
import com.weplay.sync.ui.theme.AtelierTextPrimary
import com.weplay.sync.ui.theme.AtelierTextSecondary
import com.weplay.sync.ui.theme.AtelierTray
import com.weplay.sync.ui.theme.ElectricCobalt
import com.weplay.sync.ui.theme.SignalVermilion
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
            .background(AtelierObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // 1. Navigation Header
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
                    color = AtelierTextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "CLOSE ✕",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = AtelierTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Destination Routing Matrix",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
            color = AtelierTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$activeCount active endpoints receiving synchronized stream",
            style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = ElectricCobalt
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Master Attenuation Chassis Unit
        InstrumentChassisCard(
            label = "MASTER BUS GAIN & PEAK LEVEL",
            statusIndicator = SignalVermilion
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TactileConsoleFader(
                    value = if (isMasterMuted) 0f else masterVolume,
                    onValueChange = onMasterVolumeChange,
                    label = "GLOBAL ANALOG ATTENUATION",
                    modifier = Modifier.weight(1f),
                    enabled = !isMasterMuted
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AtelierTray)
                        .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                        .clickable { onMasterMuteToggle() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMasterMuted) "UNMUTE" else "MUTE ALL",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = if (isMasterMuted) SignalVermilion else AtelierTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stereo Progressive Peak VU Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "L/R",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = AtelierTextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                TactileVUMeter(
                    level = if (isMasterMuted) 0f else masterVolume,
                    isPlaying = !isMasterMuted,
                    barCount = 18,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Routing Rack Header with Probe Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ASSIGNABLE HARDWARE SINKS",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = AtelierTextSecondary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                    .clickable { onScanDevices() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "+ SCAN SINKS",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = ElectricCobalt
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Output Route Cards
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(outputs, key = { it.id }) { device ->
                OutputRouteCard(
                    device = device,
                    onToggleSelect = onToggleDevice,
                    onVolumeChange = onDeviceVolumeChange
                )
            }
        }
    }
}
