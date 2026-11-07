package com.weplay.sync.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 1: Master Splash Screen.
 * Cinematic, high-fidelity brand introduction:
 * - Pure obsidian acoustic environment
 * - Dynamic concentric sonar / soundwave propagation
 * - Glowing 4-pillar sound mark with subtle reactive breathing
 * - "WePlay" editorial title + "Dual Audio & Sound Sharing"
 * - Official "Powered by Novyrax" footer
 */
@Composable
fun WePlaySplashScreen(
    isReady: Boolean = true,
    reduceMotion: Boolean = false,
    onAnimationComplete: () -> Unit
) {
    val markScale = remember { Animatable(0.72f) }
    val markAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(18f) }
    val footerAlpha = remember { Animatable(0f) }

    val smoothEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "AcousticPulse")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    LaunchedEffect(isReady) {
        if (!isReady) return@LaunchedEffect

        if (reduceMotion) {
            markScale.snapTo(1f)
            markAlpha.snapTo(1f)
            textAlpha.snapTo(1f)
            textOffsetY.snapTo(0f)
            footerAlpha.snapTo(1f)
            delay(500)
            onAnimationComplete()
            return@LaunchedEffect
        }

        // 1. Reveal glowing 4-pillar mark with smooth elastic expansion
        launch {
            markAlpha.animateTo(1f, tween(420, easing = smoothEasing))
        }
        launch {
            markScale.animateTo(1f, tween(580, easing = FastOutSlowInEasing))
        }

        // 2. Reveal "WePlay" + "Dual Audio & Sound Sharing"
        launch {
            delay(220)
            textAlpha.animateTo(1f, tween(450, easing = smoothEasing))
        }
        launch {
            delay(220)
            textOffsetY.animateTo(0f, tween(450, easing = smoothEasing))
        }

        // 3. Reveal "Powered by Novyrax" footer
        launch {
            delay(380)
            footerAlpha.animateTo(1f, tween(400, easing = smoothEasing))
        }

        delay(1400)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayObsidian),
        contentAlignment = Alignment.Center
    ) {
        // Acoustic concentric resonant wavefield
        AcousticResonanceField(
            modifier = Modifier.fillMaxSize(),
            phase = wavePhase
        )

        // Center Brand Identity
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-20).dp)
        ) {
            // Glowing 4-Pillar Sound Mark with ambient bloom
            Box(
                modifier = Modifier
                    .alpha(markAlpha.value)
                    .offset(y = (24 * (1f - markScale.value)).dp),
                contentAlignment = Alignment.Center
            ) {
                // Soft background cyan bloom
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                WePlayTealPrimary.copy(alpha = 0.22f * markAlpha.value),
                                WePlayTealGlow.copy(alpha = 0.08f * markAlpha.value),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.width * 0.5f
                        )
                    )
                }

                WePlayMark(
                    size = 68.dp,
                    glowAlpha = 0.75f,
                    waveformProgress = 1f
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Typography Container
            Column(
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .offset(y = textOffsetY.value.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WePlay",
                    color = WePlayTextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Dual Audio & Sound Sharing",
                    color = WePlayTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp
                )
            }
        }

        // Footer: Powered by Novyrax
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .alpha(footerAlpha.value),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NovyraxMarkGlyph(
                size = 14.dp,
                tint = WePlayTealPrimary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "POWERED BY ",
                color = Color(0xFF5E6F80),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp
            )

            Text(
                text = "NOVYRAX",
                color = WePlayTealPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )
        }
    }
}

/**
 * Concentric acoustic wave field radiating outward from center.
 */
@Composable
private fun AcousticResonanceField(
    modifier: Modifier = Modifier,
    phase: Float
) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f - 20.dp.toPx()
        val maxRadius = size.width * 0.72f
        val ringCount = 3

        for (i in 0 until ringCount) {
            val progress = ((phase + (i.toFloat() / ringCount)) % 1f)
            val radius = 48.dp.toPx() + (progress * (maxRadius - 48.dp.toPx()))
            val alpha = (1f - progress).coerceIn(0f, 1f) * 0.16f

            drawCircle(
                color = Color(0xFF21CDB8).copy(alpha = alpha),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(
                    width = (2.2f - (progress * 1.2f)).coerceAtLeast(0.8f).dp.toPx()
                )
            )
        }
    }
}

/**
 * Sleek geometric Novyrax brand mark.
 */
@Composable
fun NovyraxMarkGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    tint: Color = Color(0xFF21CDB8)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = (w * 0.14f).coerceAtLeast(1.4f)

        // Geometric dynamic N sonic wave form
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.85f)
            lineTo(w * 0.18f, h * 0.15f)
            lineTo(w * 0.82f, h * 0.85f)
            lineTo(w * 0.82f, h * 0.15f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Pulsing center nexus
        drawCircle(
            color = Color(0xFF00F5D4),
            radius = w * 0.12f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
    }
}
