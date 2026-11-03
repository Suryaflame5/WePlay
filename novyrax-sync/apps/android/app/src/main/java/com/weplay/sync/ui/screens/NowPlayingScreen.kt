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
    var progressFraction by remember { mutableFloatStateOf(0.16f) } // 00:32 of 03:24

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
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, _ ->
                                val frac = (change.position.x / barWidthPx).coerceIn(0f, 1f)
                                progressFraction = frac
                                onSeek((frac * 204000L).toLong())
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
                        text = "00:32",
                        fontSize = 11.sp,
                        color = WePlayTextMuted
                    )
                    Text(
                        text = "03:24",
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
                    onClick = { /* trigger EQ */ },
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
