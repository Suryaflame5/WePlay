package com.weplay.sync.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.model.SyncModeSetting
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.sin

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
    val infiniteTransition = rememberInfiniteTransition(label = "oscilloscope")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F14))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        // 1. Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF19202A))
                    .border(1.dp, Color(0xFF283442), RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFFF1F5F9)
                )
            }

            Text(
                text = "ENGINE SPECIFICATION & CALIBRATION",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF00E5C8)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Oscilloscope Signal Trace Canvas (Hardware clock drift & phase alignment)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF111722))
                .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Studio Reticle Grid Lines
                for (xStep in 1..7) {
                    val x = w * (xStep / 8f)
                    drawLine(
                        color = Color(0xFF1A2332),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (yStep in 1..3) {
                    val y = h * (yStep / 4f)
                    drawLine(
                        color = Color(0xFF1A2332),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Reference Clock Trace (Subtle Grey)
                val refPath = Path()
                for (px in 0..w.toInt() step 4) {
                    val angle = (px / w) * (4 * Math.PI) + phase
                    val py = (h / 2f) + (sin(angle) * (h * 0.22f)).toFloat()
                    if (px == 0) refPath.moveTo(0f, py) else refPath.lineTo(px.toFloat(), py)
                }
                drawPath(refPath, color = Color(0xFF334155), style = Stroke(width = 1.5.dp.toPx()))

                // Master Synchronized Waveform (Cyan Glow)
                val syncPath = Path()
                for (px in 0..w.toInt() step 3) {
                    val angle = (px / w) * (4 * Math.PI) + phase + 0.15f
                    val py = (h / 2f) + (sin(angle) * (h * 0.28f)).toFloat()
                    if (px == 0) syncPath.moveTo(0f, py) else syncPath.lineTo(px.toFloat(), py)
                }
                drawPath(
                    syncPath,
                    color = Color(0xFF00E5C8),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Top Status Label
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5C8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HARDWARE OSCILLOSCOPE TRACE",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
                Text(
                    text = "Δt: -2.1ms • JITTER < 0.4ms",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                    color = Color(0xFF00E5C8)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Decoding & Stream Fidelity Options
        Text(
            text = "DECODING & STREAM FIDELITY",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
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

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Sync Alignment Profile
        Text(
            text = "CLOCK ALIGNMENT TARGET",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
        )

        Spacer(modifier = Modifier.height(8.dp))

        QualityCard(
            title = "Tight Alignment (Buffered Clock Sync)",
            desc = "Dynamically delays audio by 25–40ms to eliminate room echo across speakers.",
            isSelected = currentSyncMode == SyncModeSetting.TIGHT_ALIGNMENT,
            onClick = { onSyncModeChange(SyncModeSetting.TIGHT_ALIGNMENT) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        QualityCard(
            title = "Minimum Latency (Interactive)",
            desc = "Bypasses alignment buffers for instantaneous playback and gaming.",
            isSelected = currentSyncMode == SyncModeSetting.LOW_LATENCY,
            onClick = { onSyncModeChange(SyncModeSetting.LOW_LATENCY) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Diagnostics & Telemetry Rack
        Text(
            text = "HARDWARE TELEMETRY & STATS",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141922))
                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            DiagnosticMetricRow("Active Transport", diagnostics.activeTransportSummary)
            DiagnosticMetricRow("Round-Trip Latency (RTT)", "${diagnostics.measuredRttMs} ms")
            DiagnosticMetricRow("Clock Offset (θ)", "${diagnostics.clockOffsetMs} ms")
            DiagnosticMetricRow("Peak Measured Drift", "${diagnostics.maxDriftMs} ms")
            DiagnosticMetricRow("Buffer Underruns", "${diagnostics.underrunEvents}")
            DiagnosticMetricRow("Dropped Audio Frames", "${diagnostics.droppedFramesCount}")

            Spacer(modifier = Modifier.height(14.dp))

            // Calibration Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1B232E))
                    .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(6.dp))
                    .clickable { onCalibrateClocks() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TRIGGER PTP CLOCK RE-CALIBRATION",
                    style = WePlayTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF00E5C8)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QualityCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF141E28) else Color(0xFF141922))
            .border(
                1.dp,
                if (isSelected) Color(0xFF00E5C8) else Color(0xFF222B38),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = if (isSelected) Color(0xFF00E5C8) else Color(0xFFF1F5F9)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF777D86)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF00E5C8) else Color.Transparent)
                .border(
                    1.5.dp,
                    if (isSelected) Color(0xFF00E5C8) else Color(0xFF475569),
                    CircleShape
                )
        )
    }
}

@Composable
private fun DiagnosticMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color(0xFF777D86)
        )
        Text(
            text = value,
            style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
            color = Color(0xFFF1F5F9)
        )
    }
}
