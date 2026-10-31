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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.ui.components.AlbumArtPalette
import com.weplay.sync.ui.components.ArtworkPalettes
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme

data class PlayerTrackState(
    val title: String = "Unsayable",
    val artist: String = "Brambles",
    val album: String = "Charcoal",
    val durationMs: Long = 172000L,
    val currentPositionMs: Long = 64000L,
    val isPlaying: Boolean = false,
    val format: String = "FLAC",
    val sampleRate: Int = 96000,
    val bitDepth: Int = 24
)

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
    val palette = ArtworkPalettes.Charcoal

    // Rotation animation for the subtle vinyl disc
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentRotation = if (trackState.isPlaying) rotationAngle else 0f

    var showVinylMode by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var isShuffle by remember { mutableStateOf(false) }
    var isRepeat by remember { mutableStateOf(false) }

    // Waveform amplitude data representing "Unsayable" dynamic envelope
    val waveformAmplitudes = remember {
        listOf(
            0.18f, 0.25f, 0.16f, 0.32f, 0.45f, 0.38f, 0.52f, 0.65f,
            0.42f, 0.30f, 0.58f, 0.72f, 0.85f, 0.94f, 0.82f, 0.68f,
            0.75f, 0.88f, 0.95f, 1.00f, 0.92f, 0.84f, 0.70f, 0.60f,
            0.48f, 0.40f, 0.55f, 0.66f, 0.78f, 0.86f, 0.72f, 0.62f,
            0.50f, 0.42f, 0.36f, 0.28f, 0.22f, 0.18f, 0.15f, 0.12f
        )
    }

    // Ambient radial backdrop derived from artwork palette
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.dominantAccent.copy(alpha = if (WePlayTheme.colors.isDark) 0.25f else 0.14f),
                        WePlayTheme.colors.canvas.copy(alpha = 0.95f),
                        WePlayTheme.colors.canvas
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Atelier Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 22.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                }

                Text(
                    text = "Now Playing",
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = WePlayTheme.colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onOpenQueue() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "≡",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Signature Artwork Presentation (Authentic "Charcoal" Woodcut Sliced Log / Vinyl)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14171A))
                    .border(1.dp, WePlayTheme.colors.separator.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { showVinylMode = !showVinylMode },
                contentAlignment = Alignment.Center
            ) {
                if (!showVinylMode) {
                    // Authentic Brambles "Charcoal" Sliced Tree Log Cross-Section Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp)
                    ) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = size.width / 2f * 0.92f

                        // Outer bark background
                        drawCircle(
                            color = Color(0xFFD8D7CF),
                            radius = maxRadius,
                            center = center
                        )

                        // 14 concentric organic tree growth rings with subtle natural variations
                        val ringCount = 14
                        for (i in 1..ringCount) {
                            val r = maxRadius * (i.toFloat() / ringCount)
                            drawCircle(
                                color = Color(0xFF262A2E).copy(alpha = 0.22f + (i % 3) * 0.08f),
                                radius = r,
                                center = center,
                                style = Stroke(width = if (i % 4 == 0) 2.2f else 1.2f)
                            )
                        }

                        // Organic radial fracture cracks (iconic feature of Brambles' Charcoal log)
                        val crackAngles = listOf(0.35f, 1.85f, 3.40f, 4.75f)
                        crackAngles.forEach { rad ->
                            val endX = center.x + kotlin.math.cos(rad) * (maxRadius * 0.95f)
                            val endY = center.y + kotlin.math.sin(rad) * (maxRadius * 0.95f)
                            drawLine(
                                color = Color(0xFF1C1E20).copy(alpha = 0.75f),
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 2.4f,
                                cap = StrokeCap.Round
                            )
                        }

                        // Center growth pith
                        drawCircle(
                            color = Color(0xFF1C1E20),
                            radius = 4.5f,
                            center = center
                        )
                    }
                } else {
                    // Tactile Hi-Fi Vinyl Disc Mode
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .rotate(currentRotation)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        WePlayTheme.colors.surfaceSubtle,
                                        WePlayTheme.colors.separator,
                                        WePlayTheme.colors.surfaceSubtle
                                    )
                                )
                            )
                            .border(1.5.dp, WePlayTheme.colors.separator, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(WePlayTheme.colors.canvas)
                                .border(1.dp, WePlayTheme.colors.separator, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "WEPLAY",
                                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 7.sp),
                                    color = palette.dominantAccent
                                )
                                Text(
                                    text = "96k / 24b",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                    color = WePlayTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Track Title, Artist, Favorite & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Heart Favorite Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { isFavorite = !isFavorite },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "♥" else "♡",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 20.sp),
                        color = if (isFavorite) Color(0xFFF27B58) else WePlayTheme.colors.textSecondary
                    )
                }

                // Title & Artist
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = trackState.title,
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = trackState.artist,
                        style = WePlayTheme.typography.bodyLarge,
                        color = WePlayTheme.colors.textSecondary
                    )
                }

                // Options Menu (...)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { /* options menu */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "···",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 22.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Honest Telemetry Badge
            val primarySelected = activeDevices.firstOrNull { it.isSelectedForPlayback }
            val (outputCodec, transmissionQuality) = when (primarySelected?.transport) {
                TransportType.WIFI_SYNC -> Pair("LPCM 24-bit", "Lossless Wi-Fi stream")
                TransportType.BLUETOOTH_A2DP -> Pair("AAC / SBC", "Lossy Bluetooth transmission")
                TransportType.LE_AUDIO_AURACAST,
                TransportType.LE_AUDIO_UNICAST -> Pair("LC3 48kHz", "LE Audio broadcast")
                TransportType.WIRED_EXTERNAL -> Pair("Direct PCM", "Bit-perfect wired pass-through")
                else -> Pair("Built-in Audio", "Host speaker output")
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(WePlayTheme.colors.surfaceSubtle)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(3.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${trackState.format} ${trackState.sampleRate / 1000}kHz/${trackState.bitDepth}b • Out: $outputCodec ($transmissionQuality)",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Waveform Audio Amplitude Scrubber (Matching Reference Design)
            var scrubberWidthPx by remember { mutableFloatStateOf(1f) }
            val progressFraction = (trackState.currentPositionMs.toFloat() / trackState.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .onSizeChanged { scrubberWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val pct = (offset.x / scrubberWidthPx).coerceIn(0f, 1f)
                                onSeek((pct * trackState.durationMs).toLong())
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val playedColor = WePlayTheme.colors.textPrimary
                    val unplayedColor = WePlayTheme.colors.separator.copy(alpha = 0.6f)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val count = waveformAmplitudes.size
                        val totalW = size.width
                        val maxH = size.height
                        val barW = 3.5.dp.toPx()
                        val spacing = (totalW - (count * barW)) / (count - 1).coerceAtLeast(1)

                        waveformAmplitudes.forEachIndexed { i, amp ->
                            val x = i * (barW + spacing)
                            val barH = (amp * maxH).coerceAtLeast(3.dp.toPx())
                            val top = (maxH - barH) / 2f
                            val isPlayed = (x / totalW) <= progressFraction

                            drawRoundRect(
                                color = if (isPlayed) playedColor else unplayedColor,
                                topLeft = Offset(x, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTimecode(trackState.currentPositionMs),
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                    Text(
                        text = formatTimecode(trackState.durationMs),
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Tactile Playback Controls (Shuffle, |<, Play/Pause, >|, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { isShuffle = !isShuffle },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔀",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = if (isShuffle) palette.dominantAccent else WePlayTheme.colors.textSecondary
                    )
                }

                // Previous
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onPreviousTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◀◀",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                }

                // Master Play/Pause Large Circular Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.textPrimary)
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (trackState.isPlaying) "❚❚" else "▶",
                        style = WePlayTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        color = WePlayTheme.colors.canvas
                    )
                }

                // Next
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onNextTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶▶",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                }

                // Repeat
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { isRepeat = !isRepeat },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔁",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = if (isRepeat) palette.dominantAccent else WePlayTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 6. Persistent Audio Destination Access Banner
            val selectedCount = activeDevices.count { it.isSelectedForPlayback }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
                    .clickable { onOpenOutputSelector() }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (selectedCount > 0) palette.dominantAccent else WePlayTheme.colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCount == 0) "No active output route" else "$selectedCount Output Sinks In Sync",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = if (selectedCount == 0) "Tap to assign Bluetooth or Wi-Fi receivers" else activeDevices.filter { it.isSelectedForPlayback }.joinToString(", ") { it.name },
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = WePlayTheme.colors.textSecondary,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "MANAGE ROOM ›",
                        style = WePlayTheme.typography.labelSmall,
                        color = palette.dominantAccent
                    )
                }
            }
        }
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
