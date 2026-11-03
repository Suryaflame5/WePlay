package com.weplay.sync.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Professional Stereo LED VU Peak Meter
 * Features progressive color segmentation (Cyan/Green -> Amber -> Red peak warning)
 */
@Composable
fun TactileVUMeter(
    level: Float, // 0.0 to 1.0
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16
) {
    val animatedLevel = remember { Animatable(level) }

    LaunchedEffect(level, isPlaying) {
        val target = if (isPlaying) level.coerceIn(0.05f, 1.0f) else 0f
        animatedLevel.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 80, easing = LinearEasing)
        )
    }

    Row(
        modifier = modifier
            .height(14.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF0C0F14))
            .border(1.dp, Color(0xFF222B38), RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val threshold = (i + 1).toFloat() / barCount
            val isActive = animatedLevel.value >= threshold

            val barColor = when {
                i >= barCount - 2 -> Color(0xFFFF3B30) // Red Peak
                i >= barCount - 5 -> Color(0xFFFF9500) // Amber Warn
                else -> Color(0xFF00E5C8) // Precision Cyan
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) barColor else Color(0xFF161E27))
            )
        }
    }
}

/**
 * Precision Studio Console Fader with Etched Calibration Scale
 */
@Composable
fun TactileConsoleFader(
    value: Float, // 0.0 to 1.0
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                    color = WePlayTheme.colors.textSecondary
                )
                Text(
                    text = "${(value * 100).roundToInt()}%",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                    color = if (enabled) WePlayTheme.colors.accent else WePlayTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Fader Chassis
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .onSizeChanged { widthPx = it.width.toFloat() }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        val ratio = (offset.x / widthPx).coerceIn(0f, 1f)
                        onValueChange(ratio)
                    }
                }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        val ratio = (change.position.x / widthPx).coerceIn(0f, 1f)
                        onValueChange(ratio)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Track Recess & Etched Calibration Markers
            Canvas(modifier = Modifier.fillMaxSize()) {
                val trackHeight = 8.dp.toPx()
                val topY = (size.height - trackHeight) / 2f

                // Recessed fader groove
                drawRoundRect(
                    color = Color(0xFF0B0E13),
                    topLeft = Offset(0f, topY),
                    size = Size(size.width, trackHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Groove border
                drawRoundRect(
                    color = Color(0xFF222B38),
                    topLeft = Offset(0f, topY),
                    size = Size(size.width, trackHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Active level fill
                val activeWidth = size.width * value
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00A896), Color(0xFF00E5C8))
                    ),
                    topLeft = Offset(0f, topY),
                    size = Size(activeWidth, trackHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Etched graduation tick lines above and below
                val steps = 10
                for (s in 0..steps) {
                    val x = size.width * (s.toFloat() / steps)
                    val tickHeight = if (s % 5 == 0) 5.dp.toPx() else 3.dp.toPx()
                    drawLine(
                        color = Color(0xFF4A5568),
                        start = Offset(x, topY - tickHeight - 2.dp.toPx()),
                        end = Offset(x, topY - 2.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Machined Aluminum Fader Cap (Thumb)
            val thumbWidth = 26.dp
            val thumbOffset = ((widthPx * value) - 13.dp.value * 2.75f).coerceAtLeast(0f)

            Box(
                modifier = Modifier
                    .padding(start = (value * (widthPx - 65f)).coerceAtLeast(0f).dp / 2.75f)
                    .size(width = 24.dp, height = 28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569))
                        )
                    )
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Tactile knurled ridges in center of fader cap
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(1.5.dp)
                                .background(Color(0xFF1E293B))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Architectural Hardware Vector Icon
 * Renders precision vector equipment glyphs inspired by Teenage Engineering
 */
@Composable
fun HardwareVectorGlyph(
    type: DeviceType,
    modifier: Modifier = Modifier,
    tint: Color = WePlayTheme.colors.accent
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height

        when (type) {
            DeviceType.HEADPHONES -> {
                // Headband arc
                val path = Path().apply {
                    moveTo(w * 0.2f, h * 0.65f)
                    cubicTo(w * 0.2f, h * 0.15f, w * 0.8f, h * 0.15f, w * 0.8f, h * 0.65f)
                }
                drawPath(path, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                // Ear cushions
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.1f, h * 0.5f),
                    size = Size(w * 0.2f, h * 0.4f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.7f, h * 0.5f),
                    size = Size(w * 0.2f, h * 0.4f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
            DeviceType.SPEAKER -> {
                // Speaker enclosure
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.2f, h * 0.1f),
                    size = Size(w * 0.6f, h * 0.8f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Tweeter
                drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.35f))
                // Woofer cone
                drawCircle(
                    color = tint,
                    radius = w * 0.18f,
                    center = Offset(w * 0.5f, h * 0.65f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(color = tint, radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.65f))
            }
            DeviceType.NETWORK_RECEIVER, DeviceType.HI_FI_SYSTEM -> {
                // Slim rack unit chassis
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.1f, h * 0.25f),
                    size = Size(w * 0.8f, h * 0.5f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Front display window
                drawRect(
                    color = tint,
                    topLeft = Offset(w * 0.2f, h * 0.38f),
                    size = Size(w * 0.25f, h * 0.24f)
                )
                // Rotary knob
                drawCircle(color = tint, radius = w * 0.1f, center = Offset(w * 0.7f, h * 0.5f))
            }
            else -> {
                // Clean host phone outline with sonic pulses
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.25f, h * 0.12f),
                    size = Size(w * 0.5f, h * 0.76f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.78f))
            }
        }
    }
}

/**
 * Interactive Spatial Acoustic Soundstage Canvas
 * Visualizes the central Host Phone broadcasting live audio wavefronts
 * to orbiting listeners with real-time drift, distance, and signal indicators.
 */
@Composable
fun SpatialAcousticSoundstage(
    hostName: String,
    listeners: List<ConnectedListener>,
    isLive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundstage")

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseWave"
    )

    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0C1017))
            .border(1.dp, Color(0xFF1E2734), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension * 0.44f

            // 1. Radar background grid & degree crosshairs
            drawLine(
                color = Color(0xFF1B2330),
                start = Offset(center.x, 16.dp.toPx()),
                end = Offset(center.x, size.height - 16.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0xFF1B2330),
                start = Offset(16.dp.toPx(), center.y),
                end = Offset(size.width - 16.dp.toPx(), center.y),
                strokeWidth = 1.dp.toPx()
            )

            // 2. Stationary acoustic zone orbits
            listOf(0.35f, 0.65f, 0.95f).forEach { fraction ->
                drawCircle(
                    color = Color(0xFF1A222F),
                    radius = maxRadius * fraction,
                    center = center,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)
                    )
                )
            }

            // 3. Dynamic expanding wavefronts if active
            if (isLive) {
                for (wave in 0..2) {
                    val waveProgress = (pulsePhase + (wave * 0.333f)) % 1f
                    val currentRadius = maxRadius * waveProgress
                    val alpha = (1f - waveProgress) * 0.55f

                    drawCircle(
                        color = Color(0xFF00E5C8).copy(alpha = alpha),
                        radius = currentRadius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // 4. Center Host Node Halo
            drawCircle(
                color = Color(0xFF00E5C8).copy(alpha = if (isLive) 0.18f else 0.06f),
                radius = 28.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF00E5C8),
                radius = 16.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF0C1017),
                radius = 7.dp.toPx(),
                center = center
            )
        }

        // Host Badge Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 70.dp)
        ) {
            Text(
                text = "AUDIO BROADCASTER",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF00E5C8)
            )
            Text(
                text = hostName,
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 12.sp),
                color = Color(0xFFF1F5F9)
            )
        }

        // Top Status Header Overlay
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isLive) Color(0xFF00E5C8) else Color(0xFFFF9500))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isLive) "ACOUSTIC SOUNDSTAGE ACTIVE" else "STANDBY • WAITING FOR START",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                    color = Color(0xFF94A3B8)
                )
            }
            Text(
                text = "${listeners.size} DESTINATIONS",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                color = Color(0xFF00E5C8)
            )
        }
    }
}

/**
 * Hi-Fi Equipment Rotary Control Dial
 * Inspired by Devialet Phantom / Bang & Olufsen precision acoustic dials
 * Supports interactive drag gesture around circle, circular arc readout, and central value
 */
@Composable
fun DevialetAcousticDial(
    volumePercent: Int, // 0 to 100
    onVolumeChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "MASTER ACOUSTIC ATTENUATION"
) {
    var isDragging by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
            color = Color(0xFF777D86)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .background(Color(0xFF0E1217))
                .border(1.dp, Color(0xFF263238), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val angleRad = kotlin.math.atan2(offset.y - center.y, offset.x - center.x)
                            var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat() + 90f
                            if (angleDeg < 0) angleDeg += 360f
                            val newVol = ((angleDeg / 360f) * 100f).roundToInt().coerceIn(0, 100)
                            onVolumeChange(newVol)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { _, dragAmount ->
                            val delta = (dragAmount * 0.4f).roundToInt()
                            val newVol = (volumePercent + delta).coerceIn(0, 100)
                            onVolumeChange(newVol)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Dial Canvas: outer arc, ticks, and inner brushed ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = size.width / 2f - 14.dp.toPx()
                val innerRadius = outerRadius - 22.dp.toPx()

                // Background track arc
                drawCircle(
                    color = Color(0xFF1E2836),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Active volume sweep arc
                val sweepAngle = (volumePercent / 100f) * 360f
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(Color(0xFF24B9A6), Color(0xFF5878D9), Color(0xFF00E5C8))
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Graduation ticks around circumference (48 ticks)
                val totalTicks = 48
                for (i in 0 until totalTicks) {
                    val angle = (i.toFloat() / totalTicks) * 2 * Math.PI.toFloat() - (Math.PI / 2).toFloat()
                    val isMajor = i % 4 == 0
                    val tickLen = if (isMajor) 7.dp.toPx() else 4.dp.toPx()
                    val startR = outerRadius + 5.dp.toPx()
                    val endR = startR + tickLen

                    val startX = center.x + cos(angle) * startR
                    val startY = center.y + sin(angle) * startR
                    val endX = center.x + cos(angle) * endR
                    val endY = center.y + sin(angle) * endR

                    val tickActive = (i.toFloat() / totalTicks) <= (volumePercent / 100f)
                    drawLine(
                        color = if (tickActive) Color(0xFF00E5C8) else Color(0xFF334155),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                    )
                }

                // Inner brushed ring
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF202936), Color(0xFF131922)),
                        center = center,
                        radius = innerRadius
                    ),
                    radius = innerRadius,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF334155),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Center Acoustic Readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$volumePercent",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 44.sp),
                    color = Color(0xFFF1F5F9)
                )
                Text(
                    text = "LEVEL",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 2.sp),
                    color = Color(0xFF00E5C8)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tactile EQ Quick-Adjusters (Bass & Treble)
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF161C24))
                    .border(1.dp, Color(0xFF263238), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "BASS",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF777D86)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+2.0 dB",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = Color(0xFF00E5C8)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF161C24))
                    .border(1.dp, Color(0xFF263238), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "TREBLE",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF777D86)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FLAT",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = Color(0xFFF1F5F9)
                )
            }
        }
    }
}

/**
 * Concentric Orbital Acoustic Soundstage (EchoTalk & PolySpeaker inspired)
 * Renders multiple orbital rings with device node satellites around central broadcast beacon
 */
@Composable
fun EchoOrbitSoundstage(
    modifier: Modifier = Modifier,
    broadcasterName: String = "Host Phone",
    listenerNodes: List<String> = listOf("Robin's LE Buds", "Studio Hi-Fi", "WH-1000XM5", "Living Room")
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit_pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0E14))
            .border(1.dp, Color(0xFF1E2836), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.width / 2f * 0.85f

            // Concentric orbit rings
            val orbitRatios = listOf(0.32f, 0.58f, 0.84f)
            orbitRatios.forEach { ratio ->
                drawCircle(
                    color = Color(0xFF1B2433),
                    radius = maxR * ratio,
                    center = center,
                    style = Stroke(
                        width = 1.2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 6f), 0f)
                    )
                )
            }

            // Radiating acoustic waves
            for (w in 0..2) {
                val waveFraction = (pulseProgress + (w * 0.333f)) % 1f
                val r = maxR * waveFraction
                val alpha = (1f - waveFraction) * 0.45f
                drawCircle(
                    color = Color(0xFF24B9A6).copy(alpha = alpha),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Center Host Beacon
            drawCircle(
                color = Color(0xFF24B9A6).copy(alpha = 0.2f),
                radius = 32.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF24B9A6),
                radius = 18.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF0B0E14),
                radius = 8.dp.toPx(),
                center = center
            )

            // Draw orbiting device satellites
            val angles = listOf(35.0, 140.0, 220.0, 310.0)
            listenerNodes.take(4).forEachIndexed { index, nodeName ->
                val angleDeg = angles[index % angles.size] + (pulseProgress * 360f * (if (index % 2 == 0) 0.1f else -0.1f))
                val angleRad = Math.toRadians(angleDeg)
                val distRatio = orbitRatios[index % orbitRatios.size]
                val nodeR = maxR * distRatio
                val nodeX = center.x + (cos(angleRad) * nodeR).toFloat()
                val nodeY = center.y + (sin(angleRad) * nodeR).toFloat()

                // Satellite halo
                drawCircle(
                    color = Color(0xFF5878D9).copy(alpha = 0.35f),
                    radius = 14.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
                drawCircle(
                    color = Color(0xFF5878D9),
                    radius = 9.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
                drawCircle(
                    color = Color(0xFFF1F5F9),
                    radius = 3.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
            }
        }

        // Broadcaster Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            Text(
                text = broadcasterName.uppercase(),
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF00E5C8)
            )
            Text(
                text = "${listenerNodes.size} Orbiting Synchronized Sinks",
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF777D86)
            )
        }
    }
}
