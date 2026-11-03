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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.model.SyncModeSetting
import com.weplay.sync.ui.components.EchoOrbitSoundstage
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.AtelierChamfer
import com.weplay.sync.ui.theme.AtelierGunmetal
import com.weplay.sync.ui.theme.AtelierObsidian
import com.weplay.sync.ui.theme.AtelierTextPrimary
import com.weplay.sync.ui.theme.AtelierTextSecondary
import com.weplay.sync.ui.theme.AtelierTray
import com.weplay.sync.ui.theme.ElectricCobalt
import com.weplay.sync.ui.theme.PrecisionCyan
import com.weplay.sync.ui.theme.SignalVermilion
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun OnboardingScreen(
    onComplete: (AudioQualitySetting, SyncModeSetting) -> Unit
) {
    var bluetoothGranted by remember { mutableStateOf(true) }
    var audioGranted by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtelierObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // 1. Atelier Identity Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayMark(size = 32.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "WEPLAY",
                        style = WePlayTheme.typography.displayMedium.copy(fontSize = 17.sp, letterSpacing = 1.2.sp),
                        color = AtelierTextPrimary
                    )
                    Text(
                        text = "ATELIER INSTRUMENTARIUM",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, color = AtelierTextSecondary)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "SPEC 1.0",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = PrecisionCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Concentric Orbit Hero
        EchoOrbitSoundstage(
            broadcasterName = "WePlay Master Transmitter",
            listenerNodes = listOf("Robin's LE Buds", "Studio Hi-Fi", "WH-1000XM5", "Living Room")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Editorial Value Proposition
        Text(
            text = "One phone.\nEvery listener.",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 28.sp, lineHeight = 34.sp),
            color = AtelierTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Broadcast your phone's audio — Spotify, YouTube, games, or lossless local FLAC — simultaneously to multiple Bluetooth headphones and Wi-Fi receivers in sub-millisecond sync.",
            style = WePlayTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
            color = AtelierTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. 4-Step Modular Architecture
        Text(
            text = "HOW IT OPERATES",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
            color = SignalVermilion
        )

        Spacer(modifier = Modifier.height(8.dp))

        WorkflowStepCard(
            step = "01",
            title = "Start Audio Broadcast",
            description = "Capture authorized device audio or launch studio lossless master."
        )

        Spacer(modifier = Modifier.height(6.dp))

        WorkflowStepCard(
            step = "02",
            title = "Pair Via Matrix or PIN",
            description = "Nearby listener phones scan the QR code to join without pairing hassle."
        )

        Spacer(modifier = Modifier.height(6.dp))

        WorkflowStepCard(
            step = "03",
            title = "Stream Wirelessly",
            description = "Audio packets distribute over low-latency UDP Wi-Fi and Bluetooth LE."
        )

        Spacer(modifier = Modifier.height(6.dp))

        WorkflowStepCard(
            step = "04",
            title = "Per-Listener Console Faders",
            description = "Each listener controls personal attenuation with sample-accurate clock sync."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Hardware Permissions
        Text(
            text = "HARDWARE PERMISSIONS",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
            color = AtelierTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        PermissionRow(
            title = "Nearby Bluetooth & LE Audio",
            subtitle = "Required to discover and bind wireless headphones",
            isGranted = bluetoothGranted,
            onToggle = { bluetoothGranted = !bluetoothGranted }
        )

        Spacer(modifier = Modifier.height(6.dp))

        PermissionRow(
            title = "Local High-Res Audio Storage",
            subtitle = "Required to decode 24-bit/96kHz studio FLAC & WAV",
            isGranted = audioGranted,
            onToggle = { audioGranted = !audioGranted }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 6. Action Button: Machined Signal Vermilion capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(SignalVermilion, Color(0xFFD63000))
                    )
                )
                .clickable {
                    onComplete(AudioQualitySetting.SOURCE_PRESERVING, SyncModeSetting.TIGHT_ALIGNMENT)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ENTER WEPLAY ATELIER",
                    style = WePlayTheme.typography.titleMedium.copy(
                        fontSize = 12.sp,
                        letterSpacing = 1.4.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "→",
                    style = WePlayTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun WorkflowStepCard(
    step: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AtelierGunmetal)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AtelierTray)
                .border(1.dp, SignalVermilion, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                color = SignalVermilion
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = AtelierTextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = description,
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = AtelierTextSecondary
            )
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AtelierGunmetal)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
            .clickable { onToggle() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = AtelierTextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = AtelierTextSecondary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isGranted) PrecisionCyan else AtelierTray)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isGranted) "READY" else "ENABLE",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = if (isGranted) Color(0xFF0C1017) else AtelierTextSecondary
            )
        }
    }
}
