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
import com.weplay.sync.ui.components.PlanetaryRotaryDial
import com.weplay.sync.ui.theme.AtelierChamfer
import com.weplay.sync.ui.theme.AtelierGunmetal
import com.weplay.sync.ui.theme.AtelierObsidian
import com.weplay.sync.ui.theme.AtelierTextPrimary
import com.weplay.sync.ui.theme.AtelierTextSecondary
import com.weplay.sync.ui.theme.AtelierTray
import com.weplay.sync.ui.theme.CalibratedGold
import com.weplay.sync.ui.theme.ElectricCobalt
import com.weplay.sync.ui.theme.PrecisionCyan
import com.weplay.sync.ui.theme.SignalVermilion
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
    // 0 = ART-DIRECTED STUDIO, 1 = HOROLOGICAL ROTARY DIAL, 2 = VINYL DECK
    var viewMode by remember { mutableIntStateOf(0) }
    var dialVolume by remember { mutableIntStateOf(78) }
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
            .background(AtelierObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AtelierTray)
                        .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 20.sp),
                        color = AtelierTextPrimary
                    )
                }

                // Center Title with Mode Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LISTENING CHAMBER",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.4.sp),
                        color = SignalVermilion
                    )
                    Text(
                        text = when (viewMode) {
                            1 -> "PLANETARY HOROLOGY DIAL"
                            2 -> "VINYL TRANSPORT DECK"
                            else -> "ART-DIRECTED STUDIO MASTER"
                        },
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 9.sp),
                        color = AtelierTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AtelierTray)
                        .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                        .clickable { onOpenQueue() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "≡",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        color = AtelierTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. View Mode Switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
                    .padding(3.dp)
            ) {
                listOf("MASTER ART", "HOROLOGY DIAL", "VINYL").forEachIndexed { index, label ->
                    val isSelected = viewMode == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) SignalVermilion else Color.Transparent)
                            .clickable { viewMode = index }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = WePlayTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp,
                                color = if (isSelected) Color.White else AtelierTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Central Interactive Object Viewport
            when (viewMode) {
                1 -> {
                    // Horological Planetary Rotary Dial
                    PlanetaryRotaryDial(
                        valuePercent = dialVolume,
                        onValueChange = { dialVolume = it },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                2 -> {
                    // Hi-Fi Vinyl Deck
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AtelierGunmetal)
                            .border(1.dp, AtelierChamfer, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(210.dp)
                                .rotate(currentRotation)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            AtelierTray,
                                            AtelierObsidian,
                                            AtelierTray,
                                            Color(0xFF070A0F)
                                        )
                                    )
                                )
                                .border(2.dp, AtelierChamfer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(SignalVermilion)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "WEPLAY",
                                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "96k / 24b",
                                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Sliced Woodcut Charcoal Master
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AtelierGunmetal)
                            .border(1.dp, AtelierChamfer, RoundedCornerShape(10.dp)),
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
                                color = Color(0xFFDEDBD2),
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

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Track Metadata & Studio Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AtelierTray)
                        .clickable { isFavorite = !isFavorite },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "♥" else "♡",
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 18.sp),
                        color = if (isFavorite) SignalVermilion else AtelierTextSecondary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = trackState.title,
                        style = WePlayTheme.typography.displayLarge.copy(fontSize = 22.sp),
                        color = AtelierTextPrimary
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "${trackState.artist} • ${trackState.album}",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = AtelierTextSecondary
                    )
                }

                // Lossless Calibrated Gold Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(AtelierTray)
                        .border(1.dp, CalibratedGold, RoundedCornerShape(3.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "FLAC 96/24",
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                        color = CalibratedGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Tactile Waveform Scrubber
            var scrubberWidthPx by remember { mutableFloatStateOf(1f) }
            val progressFraction = (trackState.currentPositionMs.toFloat() / trackState.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AtelierTray)
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
                        val barW = 3.5.dp.toPx()
                        val spacing = (totalW - (count * barW)) / (count - 1).coerceAtLeast(1)

                        waveformAmplitudes.forEachIndexed { i, amp ->
                            val x = i * (barW + spacing)
                            val barH = (amp * maxH).coerceAtLeast(3.dp.toPx())
                            val top = (maxH - barH) / 2f
                            val isPlayed = (x / totalW) <= progressFraction

                            drawRoundRect(
                                color = if (isPlayed) SignalVermilion else AtelierChamfer,
                                topLeft = Offset(x, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTimecode(trackState.currentPositionMs),
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                        color = AtelierTextPrimary
                    )
                    Text(
                        text = formatTimecode(trackState.durationMs),
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                        color = AtelierTextSecondary
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isShuffle) SignalVermilion else AtelierTray)
                        .clickable { isShuffle = !isShuffle },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SHUF",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = if (isShuffle) Color.White else AtelierTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AtelierTray)
                        .clickable { onPreviousTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◀◀",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = AtelierTextPrimary
                    )
                }

                // Master Play/Pause Large Center Capsule
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(SignalVermilion, Color(0xFFD63000))
                            )
                        )
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (trackState.isPlaying) "❚❚" else "▶",
                        style = WePlayTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AtelierTray)
                        .clickable { onNextTrack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶▶",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = AtelierTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isRepeat) SignalVermilion else AtelierTray)
                        .clickable { isRepeat = !isRepeat },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RPT",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = if (isRepeat) Color.White else AtelierTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Dynamic Audio Routing Capsule
            val selectedCount = activeDevices.count { it.isSelectedForPlayback }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AtelierGunmetal)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(8.dp))
                    .clickable { onOpenOutputSelector() }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (selectedCount > 0) PrecisionCyan else AtelierTextSecondary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCount == 0) "No active output route" else "$selectedCount Sinks In Clock Sync",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 12.sp),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = if (selectedCount == 0) "Tap to assign Bluetooth or Wi-Fi receivers" else activeDevices.filter { it.isSelectedForPlayback }.joinToString(", ") { it.name },
                            style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = AtelierTextSecondary,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "ROUTING ›",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = ElectricCobalt
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
