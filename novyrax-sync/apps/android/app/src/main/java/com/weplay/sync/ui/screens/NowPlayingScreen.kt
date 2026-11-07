package com.weplay.sync.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.VinylAlbumCover
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlayTealDark
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

data class PlayerTrackState(
    val title: String = "Midnight Horizons",
    val artist: String = "Ambient Atlas",
    val album: String = "Atlas",
    val durationMs: Long = 204000L,
    val currentPositionMs: Long = 32000L,
    val isPlaying: Boolean = true,
    val format: String = "FLAC",
    val sampleRate: Int = 96000,
    val bitDepth: Int = 24
)

/**
 * Screen 6: Master Now Playing Screen ("Midnight Horizons - Ambient Atlas").
 * Exact visual match to Screen 6 in the design reference:
 * - Top Bar: Chevron down "v" + Route icon
 * - Hero: Cosmic Alien Landscape Album Sleeve with physical vinyl record sliding out
 * - Track Title: "Midnight Horizons" (bold white) + "Ambient Atlas" (subdued grey)
 * - Scrubber Track: Orange progress bar with "00:32" & "03:24" timestamps
 * - Transport Controls: Shuffle, Previous, Radiant Molten Orange Play/Pause Circle, Next, Repeat
 * - Bottom Quick Action Pills: "[Route]", "[Equalizer]", "[Audio Devices]"
 */
@Composable
fun NowPlayingScreen(
    trackState: PlayerTrackState,
    activeDevices: List<AudioOutputDevice>,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onOpenOutputSelector: () -> Unit,
    onOpenQueue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = trackState.durationMs.coerceAtLeast(1000L)
    val currentPos = trackState.currentPositionMs.coerceIn(0L, duration)
    val progressFraction = (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

    val curSec = (currentPos / 1000L)
    val durSec = (duration / 1000L)
    val curTimeStr = String.format("%02d:%02d", curSec / 60, curSec % 60)
    val durTimeStr = String.format("%02d:%02d", durSec / 60, durSec % 60)

    var showEqualizerModal by remember { mutableStateOf(false) }
    var bassBoostFraction by remember { mutableFloatStateOf(0.55f) }
    var spatializerFraction by remember { mutableFloatStateOf(0.70f) }
    var selectedPreset by remember { mutableStateOf("Studio Ref") }
    var eqGains by remember { mutableStateOf(floatArrayOf(2.5f, 1.0f, -0.5f, 1.5f, 3.0f)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: Chevron Down & Route Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    tint = WePlayTextPrimary,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onBack() }
                )

                WePlayIcon(
                    type = WePlayIconType.ROUTE,
                    tint = WePlayTextPrimary,
                    size = 20.dp,
                    modifier = Modifier.clickable { onOpenOutputSelector() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Vinyl Album Cover Hero (Cosmic landscape sleeve + sliding vinyl)
            VinylAlbumCover(
                isPlaying = trackState.isPlaying,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Track Metadata
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Midnight Horizons",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ambient Atlas",
                    fontSize = 14.sp,
                    color = WePlayTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Scrubber Progress Bar & Timestamps ("00:32" / "03:24")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ) {
                var barWidthPx by remember { mutableFloatStateOf(1f) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .onSizeChanged { barWidthPx = it.width.toFloat() }
                        .pointerInput(duration) {
                            detectHorizontalDragGestures { change, _ ->
                                val frac = (change.position.x / barWidthPx).coerceIn(0f, 1f)
                                onSeek((frac * duration).toLong())
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = this.size.width
                        val h = this.size.height
                        val trackH = 3.dp.toPx()
                        val thumbR = 5.dp.toPx()
                        val cy = h * 0.5f

                        // Background inactive track
                        drawRoundRect(
                            color = Color(0xFF1E2638),
                            topLeft = Offset(0f, cy - trackH * 0.5f),
                            size = Size(w, trackH),
                            cornerRadius = CornerRadius(trackH * 0.5f, trackH * 0.5f)
                        )

                        // Played active track
                        val activeW = w * progressFraction
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(WePlayOrangePrimary, WePlayOrangeGlow)
                            ),
                            topLeft = Offset(0f, cy - trackH * 0.5f),
                            size = Size(activeW, trackH),
                            cornerRadius = CornerRadius(trackH * 0.5f, trackH * 0.5f)
                        )

                        // Glowing orange thumb
                        drawCircle(
                            color = WePlayOrangePrimary,
                            radius = thumbR,
                            center = Offset(activeW.coerceIn(thumbR, w - thumbR), cy)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = curTimeStr,
                        fontSize = 11.sp,
                        color = WePlayTextMuted
                    )
                    Text(
                        text = durTimeStr,
                        fontSize = 11.sp,
                        color = WePlayTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Playback Transport Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Icon
                WePlayIcon(
                    type = WePlayIconType.ROUTE,
                    tint = WePlayTextSecondary,
                    size = 20.dp
                )

                // Previous
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onPreviousTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width
                        val h = size.height
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.85f, h * 0.15f)
                            lineTo(w * 0.35f, h * 0.50f)
                            lineTo(w * 0.85f, h * 0.85f)
                            close()
                        }
                        drawPath(path, Color.White)
                        drawRect(Color.White, Offset(w * 0.15f, h * 0.15f), Size(w * 0.12f, h * 0.70f))
                    }
                }

                // Large Radiant Molten Orange Play/Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    WePlayOrangeGlow,
                                    WePlayOrangePrimary,
                                    WePlayOrangeDark
                                )
                            )
                        )
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    if (trackState.isPlaying) {
                        // Pause bars
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val w = size.width
                            val h = size.height
                            drawRoundRect(Color.White, Offset(w * 0.22f, h * 0.15f), Size(w * 0.20f, h * 0.70f), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
                            drawRoundRect(Color.White, Offset(w * 0.58f, h * 0.15f), Size(w * 0.20f, h * 0.70f), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Next
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onNextTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width
                        val h = size.height
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.15f, h * 0.15f)
                            lineTo(w * 0.65f, h * 0.50f)
                            lineTo(w * 0.15f, h * 0.85f)
                            close()
                        }
                        drawPath(path, Color.White)
                        drawRect(Color.White, Offset(w * 0.73f, h * 0.15f), Size(w * 0.12f, h * 0.70f))
                    }
                }

                // Repeat Icon
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Repeat",
                    tint = WePlayTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 6. Bottom Quick-Action Pills: [Route], [Equalizer], [Audio Devices]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionPill(
                    label = "Route",
                    iconType = WePlayIconType.ROUTE,
                    onClick = onOpenOutputSelector,
                    modifier = Modifier.weight(1f)
                )
                QuickActionPill(
                    label = "Equalizer",
                    iconType = WePlayIconType.EQUALIZER,
                    onClick = { showEqualizerModal = true },
                    modifier = Modifier.weight(1f)
                )
                QuickActionPill(
                    label = "Audio Devices",
                    iconType = WePlayIconType.SPEAKER,
                    onClick = onOpenOutputSelector,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        // 7. Interactive 5-Band Studio Equalizer & Audio Enhancer Modal
        if (showEqualizerModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { showEqualizerModal = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color(0xFF0F1520))
                        .border(1.dp, WePlayBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .clickable(enabled = false) { }
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "STUDIO EQUALIZER",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WePlayTextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "5-Band Precision Parametric DSP",
                                    fontSize = 11.sp,
                                    color = WePlayTealGlow
                                )
                            }
                            Text(
                                text = "DONE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTealPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WePlayTealPrimary.copy(alpha = 0.15f))
                                    .clickable { showEqualizerModal = false }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Presets Row
                        val presets = listOf("Studio Ref", "Deep Bass", "Vocal Clear", "Club 3D")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presets.forEach { preset ->
                                val isSelected = selectedPreset == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) WePlayTealPrimary else Color(0xFF161E2C))
                                        .border(1.dp, if (isSelected) WePlayTealGlow else WePlayBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedPreset = preset
                                            eqGains = when (preset) {
                                                "Deep Bass" -> floatArrayOf(6.0f, 4.0f, 0.0f, 1.0f, 2.0f)
                                                "Vocal Clear" -> floatArrayOf(-1.0f, 0.5f, 3.5f, 4.0f, 2.0f)
                                                "Club 3D" -> floatArrayOf(5.0f, 2.0f, -1.0f, 3.0f, 4.5f)
                                                else -> floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.Black else WePlayTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 5-Band Vertical Equalizer Bars
                        val bandLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            bandLabels.forEachIndexed { i, label ->
                                val gain = eqGains[i]
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (gain >= 0) "+${String.format("%.1f", gain)}" else String.format("%.1f", gain),
                                        fontSize = 10.sp,
                                        color = WePlayTealGlow
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .width(28.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val h = size.height
                                            val cy = h * 0.5f
                                            // Vertical Slot
                                            drawLine(
                                                color = Color(0xFF1E2A3C),
                                                start = Offset(w * 0.5f, 0f),
                                                end = Offset(w * 0.5f, h),
                                                strokeWidth = 3.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            // Center Zero Tick
                                            drawLine(
                                                color = Color.White.copy(alpha = 0.3f),
                                                start = Offset(w * 0.2f, cy),
                                                end = Offset(w * 0.8f, cy),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                            // Slider Thumb
                                            val norm = ((gain + 12f) / 24f).coerceIn(0f, 1f)
                                            val thumbY = h * (1f - norm)
                                            drawCircle(
                                                color = WePlayTealPrimary,
                                                radius = 6.dp.toPx(),
                                                center = Offset(w * 0.5f, thumbY)
                                            )
                                            drawCircle(
                                                color = Color.White,
                                                radius = 2.dp.toPx(),
                                                center = Offset(w * 0.5f, thumbY)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 9.sp,
                                        color = WePlayTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bass Boost & 3D Spatializer Sliders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Bass Boost (${(bassBoostFraction * 100).toInt()}%)", fontSize = 11.sp, color = WePlayTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Color(0xFF161E2C))
                                        .clickable { bassBoostFraction = if (bassBoostFraction > 0.5f) 0.2f else 0.8f }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(bassBoostFraction)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(Brush.horizontalGradient(listOf(WePlayTealDark, WePlayTealPrimary)))
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "3D Spatializer (${(spatializerFraction * 100).toInt()}%)", fontSize = 11.sp, color = WePlayTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Color(0xFF161E2C))
                                        .clickable { spatializerFraction = if (spatializerFraction > 0.5f) 0.3f else 0.9f }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(spatializerFraction)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(Brush.horizontalGradient(listOf(WePlayTealDark, WePlayTealPrimary)))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    iconType: WePlayIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(WePlaySurface)
            .border(1.dp, WePlayBorder, RoundedCornerShape(19.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WePlayIcon(
                type = iconType,
                tint = WePlayTextSecondary,
                size = 14.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = WePlayTextPrimary
            )
        }
    }
}
