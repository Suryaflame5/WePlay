package com.weplay.sync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.model.SyncModeSetting
import com.weplay.sync.ui.theme.WePlayTheme

data class SyncDiagnostics(
    val measuredRttMs: Double = 14.2,
    val clockOffsetMs: Double = -2.1,
    val maxDriftMs: Double = 4.8,
    val droppedFramesCount: Long = 0,
    val underrunEvents: Long = 0,
    val activeTransportSummary: String = "1x Classic A2DP (Host) + 2x Wi-Fi LPCM (Companion)"
)

@Composable
fun AudioSyncSettingsScreen(
    currentQuality: AudioQualitySetting,
    currentSyncMode: SyncModeSetting,
    diagnostics: SyncDiagnostics,
    onQualityChange: (AudioQualitySetting) -> Unit,
    onSyncModeChange: (SyncModeSetting) -> Unit,
    onCalibrateClocks: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(WePlayTheme.spacing.xxl)
            .verticalScroll(rememberScrollState())
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .clickable { onBack() }
                    .padding(4.dp)
            ) {
                Text(
                    text = "< BACK",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(WePlayTheme.spacing.l))
            Text(
                text = "ENGINE SPECIFICATION",
                style = WePlayTheme.typography.labelSmall,
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Audio & Synchronization",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
            color = WePlayTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Calibrate playback clock offsets, audio buffer targets, and stream codecs.",
            style = WePlayTheme.typography.bodyMedium,
            color = WePlayTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // QUALITY PREFERENCE
        Text(
            text = "DECODING & STREAM FIDELITY",
            style = WePlayTheme.typography.labelSmall,
            color = WePlayTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        QualityCard(
            title = "Source-Preserving (Bit-Perfect)",
            desc = "No lossy transcoding. Direct FLAC/ALAC up to 192 kHz / 24-bit.",
            isSelected = currentQuality == AudioQualitySetting.SOURCE_PRESERVING,
            onClick = { onQualityChange(AudioQualitySetting.SOURCE_PRESERVING) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        QualityCard(
            title = "High Quality (320 kbps / 48 kHz)",
            desc = "Optimized Opus / LC3 encoding across wireless outputs.",
            isSelected = currentQuality == AudioQualitySetting.HIGH_QUALITY,
            onClick = { onQualityChange(AudioQualitySetting.HIGH_QUALITY) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        QualityCard(
            title = "Low Bandwidth (128 kbps)",
            desc = "Maximum network resilience for congested Wi-Fi environments.",
            isSelected = currentQuality == AudioQualitySetting.LOW_BANDWIDTH,
            onClick = { onQualityChange(AudioQualitySetting.LOW_BANDWIDTH) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // SYNC ALIGNMENT
        Text(
            text = "SYNC ALIGNMENT PROFILE",
            style = WePlayTheme.typography.labelSmall,
            color = WePlayTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        QualityCard(
            title = "Tight Alignment (Buffered Clock Sync)",
            desc = "Dynamically delays audio by 25–40ms to eliminate room echo.",
            isSelected = currentSyncMode == SyncModeSetting.TIGHT_ALIGNMENT,
            onClick = { onSyncModeChange(SyncModeSetting.TIGHT_ALIGNMENT) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        QualityCard(
            title = "Minimum Latency (Interactive)",
            desc = "Bypasses alignment buffers for instantaneous playback.",
            isSelected = currentSyncMode == SyncModeSetting.LOW_LATENCY,
            onClick = { onSyncModeChange(SyncModeSetting.LOW_LATENCY) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // DIAGNOSTICS & TELEMETRY PANEL
        Text(
            text = "DIAGNOSTICS & TELEMETRY",
            style = WePlayTheme.typography.labelSmall,
            color = WePlayTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
                .padding(WePlayTheme.spacing.l)
        ) {
            DiagnosticMetricRow("Active Pipeline", diagnostics.activeTransportSummary)
            DiagnosticMetricRow("Network RTT", "${diagnostics.measuredRttMs} ms")
            DiagnosticMetricRow("Clock Offset (θ)", "${diagnostics.clockOffsetMs} ms")
            DiagnosticMetricRow("Peak Measured Drift", "${diagnostics.maxDriftMs} ms")
            DiagnosticMetricRow("Buffer Underruns", "${diagnostics.underrunEvents}")
            DiagnosticMetricRow("Dropped Audio Frames", "${diagnostics.droppedFramesCount}")

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(WePlayTheme.colors.surfaceSubtle)
                    .clickable { onCalibrateClocks() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RECALIBRATE CLOCKS NOW",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.accent
                )
            }
        }
    }
}

@Composable
private fun DiagnosticMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = WePlayTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = WePlayTheme.typography.tabularMetric.copy(fontSize = 12.sp),
            color = WePlayTheme.colors.textPrimary
        )
    }
}

@Composable
private fun QualityCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(WePlayTheme.colors.surface)
            .border(
                1.dp,
                if (isSelected) WePlayTheme.colors.accent else WePlayTheme.colors.separator,
                RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(WePlayTheme.spacing.l)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = WePlayTheme.typography.titleMedium,
                color = WePlayTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(WePlayTheme.colors.accent)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = WePlayTheme.colors.textSecondary
        )
    }
}
