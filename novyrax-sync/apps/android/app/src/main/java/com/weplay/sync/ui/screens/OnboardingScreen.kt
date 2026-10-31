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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.model.SyncModeSetting
import com.weplay.sync.ui.components.WePlayMark
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
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(28.dp))

            // Atelier Identity Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayMark(size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "WEPLAY",
                        style = WePlayTheme.typography.displayMedium.copy(fontSize = 20.sp, letterSpacing = 1.2.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                    Text(
                        text = "MODERN LISTENING ATELIER",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, color = WePlayTheme.colors.textSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "One source.\nIn sync.",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
                color = WePlayTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Experience high-fidelity music playback across your compatible Bluetooth headphones, Auracast receivers, and synchronized Wi-Fi speakers.",
                style = WePlayTheme.typography.bodyLarge,
                color = WePlayTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Contextual Permission Toggles
            PermissionRow(
                title = "Nearby Devices & Bluetooth",
                subtitle = "Required to scan and bind wireless audio outputs",
                isGranted = bluetoothGranted,
                onToggle = { bluetoothGranted = !bluetoothGranted }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PermissionRow(
                title = "Local Music Storage",
                subtitle = "Required to decode lossless FLAC, ALAC & WAV files",
                isGranted = audioGranted,
                onToggle = { audioGranted = !audioGranted }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Atelier Preset Summary (Sensible defaults configured)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(WePlayTheme.colors.surfaceSubtle)
                    .padding(14.dp)
            ) {
                Text(
                    text = "Engine calibrated: Source-preserving 24-bit pass-through and tight 30ms alignment are enabled by default.",
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
        }

        // Action Button: Enter Atelier
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(WePlayTheme.colors.textPrimary)
                .clickable {
                    onComplete(AudioQualitySetting.SOURCE_PRESERVING, SyncModeSetting.TIGHT_ALIGNMENT)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ENTER ATELIER",
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp, letterSpacing = 1.sp),
                color = WePlayTheme.colors.canvas
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
            .clip(RoundedCornerShape(4.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
            .clickable { onToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WePlayTheme.typography.titleMedium,
                color = WePlayTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(if (isGranted) WePlayTheme.colors.accent else WePlayTheme.colors.surfaceSubtle)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isGranted) "READY" else "ENABLE",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = if (isGranted) WePlayTheme.colors.canvas else WePlayTheme.colors.textPrimary
            )
        }
    }
}
