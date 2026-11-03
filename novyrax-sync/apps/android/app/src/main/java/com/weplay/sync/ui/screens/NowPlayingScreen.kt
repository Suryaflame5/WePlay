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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.weplay.sync.ui.components.ArtworkPalettes
import com.weplay.sync.ui.components.DevialetAcousticDial
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

    // View mode: 0 = Art-Directed Artwork, 1 = Hi-Fi Equipment Acoustic Dial (Devialet inspired), 2 = Vinyl Disc
    var viewMode by remember { mutableIntStateOf(0) }
    var dialVolume by remember { mutableIntStateOf(75) }
    var isFavorite by remember { mutableStateOf(false) }
    var isShuffle by remember { mutableStateOf(false) }
    var isRepeat by remember { mutableStateOf(false) }

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

    val waveformAmplitudes = remember {
        listOf(
            0.18f, 0.25f, 0.16f, 0.32f, 0.45f, 0.38f, 0.52f, 0.65f,
            0.42f, 0.30f, 0.58f, 0.72f, 0.85f, 0.94f, 0.82f, 0.68f,
            0.75f, 0.88f, 0.95f, 1.00f, 0.92f, 0.84f, 0.70f, 0.60f,
            0.48f, 0.40f, 0.55f, 0.66f, 0.78f, 0.86f, 0.72f, 0.62f,
            0.50f, 0.42f, 0.36f, 0.28f, 0.22f, 0.18f, 0.15f, 0.12f
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1318))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF27B58).copy(alpha = 0.15f),
                        Color(0xFF0F1318).copy(alpha = 0.95f),
                        Color(0xFF0A0D12)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                        .background(Color(0xFF19202A))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 22.sp),
                        color = Color(0xFFF1F5F9)
                    )
                }

                // Center Title with Mode Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NOW PLAYING",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                        color = Color(0xFF00E5C8)
                    )
                    Text(
                        text = when (viewMode) {
                            1 -> "HI-FI ACOUSTIC DIAL"
                            2 -> "VINYL DECK"
                            else -> "ART-DIRECTED STUDIO"
                        },
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF777D86)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF19202A))
                        .clickable { onOpenQueue() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "≡",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = Color(0xFFF1F5F9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. View Mode Switcher Pill (ARTWORK / DIAL / VINYL)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF151C25))
                    .border(1.dp, Color(0xFF243040), RoundedCornerShape(6.dp))
                    .padding(3.dp)
            ) {
                listOf("ARTWORK", "HI-FI DIAL", "VINYL").forEachIndexed { index, label ->
                    val isSelected = viewMode == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) Color(0xFF00E5C8) else Color.Transparent)
                            .clickable { viewMode = index }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = WePlayTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp,
                                color = if (isSelected) Color(0xFF0B1017) else Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Central Interactive Presentation Box
            when (viewMode) {
                1 -> {
                    // Devialet Phantom / Bang & Olufsen-Style Acoustic Rotary Dial
                    DevialetAcousticDial(
                        volumePercent = dialVolume,
                        onVolumeChange = { dialVolume = it },
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                2 -> {
                    // Hi-Fi Vinyl Disc Deck
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141920))
                            .border(1.dp, Color(0xFF222C3A), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .rotate(currentRotation)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF2A3442),
                                            Color(0xFF121720),
                                            Color(0xFF2A3442),
                                            Color(0xFF0B0E13)
                                        )
                                    )
                                )
                                .border(2.dp, Color(0xFF334155), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF27B58))
                                    .border(2.dp, Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "WEPLAY",
                                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                                        color = Color(0xFF0E131A)
                                    )
                                    Text(
                                        text = "96k / 24b",
                                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                                        color = Color(0xFF0E131A)
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Art-Directed Music Artwork Canvas (Brambles Charcoal Sliced Woodcut)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF13171F))
                            .border(1.dp, Color(0xFF232D3B), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val maxRadius = size.width / 2f * 0.92f

                            drawCircle(
                                color = Color(0xFFE2E0D8),
                                radius = maxRadius,
                                center = center
                            )

                            val ringCount = 15
                            for (i in 1..ringCount) {
                                val r = maxRadius * (i.toFloat() / ringCount)
                                drawCircle(
                                    color = Color(0xFF222830).copy(alpha = 0.22f + (i % 3) * 0.08f),
                                    radius = r,
                                    center = center,
                                    style = Stroke(width = if (i % 4 == 0) 2.2f else 1.2f)
                                )
                            }

                            val crackAngles = listOf(0.35f, 1.85f, 3.40f, 4.75f)
                            crackAngles.forEach { rad ->
                                val endX = center.x + kotlin.math.cos(rad) * (maxRadius * 0.95f)
                                val endY = center.y + kotlin.math.sin(rad) * (maxRadius * 0.95f)
                                drawLine(
                                    color = Color(0xFF1A1F26).copy(alpha = 0.75f),
                                    start = center,
                                    end = Offset(endX, endY),
                                    strokeWidth = 2.4f,
                                    cap = StrokeCap.Round
                                )
                            }

                            drawCircle(
                                color = Color(0xFF1A1F26),
                                radius = 5f,
                                center = center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Track Metadata & Favorite Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF161C24))
                        .clickable { isFavorite = !isFavorite },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "♥" else "♡",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 20.sp),
                        color = if (isFavorite) Color(0xFFF27B58) else Color(0xFF777D86)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = trackState.title,
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 22.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${trackState.artist} • ${trackState.album}",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = Color(0xFF94A3B8)
                    )
                }

                // High-Res Audio Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF161C24))
                        .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(4.dp))
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "FLAC 96/24",
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                        color = Color(0xFF00E5C8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Tactile Waveform Amplitude Scrubber
            var scrubberWidthPx by remember { mutableFloatStateOf(1f) }
            val progressFraction = (trackState.currentPositionMs.toFloat() / trackState.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF141922))
                        .padding(horizontal = 6.dp)
                        .onSizeChanged { scrubberWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val pct = (offset.x / scrubberWidthPx).coerceIn(0f, 1f)
                                onSeek((pct * trackState.durationMs).toLong())
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val count = waveformAmplitudes.size
                        val totalW = size.width
                        val maxH = size.height
                        val barW = 4.dp.toPx()
                        val spacing = (totalW - (count * barW)) / (count - 1).coerceAtLeast(1)

                        waveformAmplitudes.forEachIndexed { i, amp ->
                            val x = i * (barW + spacing)
                            val barH = (amp * maxH).coerceAtLeast(4.dp.toPx())
                            val top = (maxH - barH) / 2f
                            val isPlayed = (x / totalW) <= progressFraction

                            drawRoundRect(
                                color = if (isPlayed) Color(0xFF00E5C8) else Color(0xFF283548),
                                topLeft = Offset(x, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
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
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = formatTimecode(trackState.durationMs),
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                        color = Color(0xFF777D86)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Machined Aluminum Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Pill
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isShuffle) Color(0xFF1E2C3D) else Color(0xFF141922))
                        .clickable { isShuffle = !isShuffle },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SHUF",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isShuffle) Color(0xFF00E5C8) else Color(0xFF777D86)
                    )
                }

                // Previous Track
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B232E))
                        .clickable { onPreviousTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◀◀",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = Color(0xFFF1F5F9)
                    )
                }

                // Master Play/Pause Large Center Capsule
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF00E5C8), Color(0xFF159E8C))
                            )
                        )
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (trackState.isPlaying) "❚❚" else "▶",
                        style = WePlayTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        color = Color(0xFF0A0E14)
                    )
                }

                // Next Track
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B232E))
                        .clickable { onNextTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶▶",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = Color(0xFFF1F5F9)
                    )
                }

                // Repeat Pill
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isRepeat) Color(0xFF1E2C3D) else Color(0xFF141922))
                        .clickable { isRepeat = !isRepeat },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RPT",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isRepeat) Color(0xFF00E5C8) else Color(0xFF777D86)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 7. Dynamic Audio Routing Capsule (B&O / Apple AirPlay style)
            val selectedCount = activeDevices.count { it.isSelectedForPlayback }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141A23))
                    .border(1.dp, Color(0xFF222C3A), RoundedCornerShape(10.dp))
                    .clickable { onOpenOutputSelector() }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (selectedCount > 0) Color(0xFF00E5C8) else Color(0xFFFF9500))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCount == 0) "No active output route" else "$selectedCount Output Sinks In Sync",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = if (selectedCount == 0) "Tap to assign Bluetooth or Wi-Fi receivers" else activeDevices.filter { it.isSelectedForPlayback }.joinToString(", ") { it.name },
                            style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8),
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "AUDIO ROOM ›",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF00E5C8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
