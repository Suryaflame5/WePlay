package com.weplay.sync.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated Native Splash Screen.
 * Total choreography duration: ~850ms.
 * Honors reduce motion settings via instant or fast-fade resolution.
 */
@Composable
fun WePlaySplashScreen(
    isReady: Boolean = true,
    reduceMotion: Boolean = false,
    onAnimationComplete: () -> Unit
) {
    val bgAlpha = remember { Animatable(0f) }
    val waveformProgress = remember { Animatable(0f) }
    val playProgress = remember { Animatable(0f) }
    val orbitProgress = remember { Animatable(0f) }
    var nodesCount by remember { mutableIntStateOf(0) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffset = remember { Animatable(6f) }
    val taglineAlpha = remember { Animatable(0f) }

    val standardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val decelerationEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

    LaunchedEffect(isReady) {
        if (!isReady) return@LaunchedEffect

        if (reduceMotion) {
            bgAlpha.snapTo(1f)
            waveformProgress.snapTo(1f)
            playProgress.snapTo(1f)
            orbitProgress.snapTo(1f)
            nodesCount = 3
            wordmarkAlpha.snapTo(1f)
            wordmarkOffset.snapTo(0f)
            taglineAlpha.snapTo(1f)
            delay(150)
            onAnimationComplete()
            return@LaunchedEffect
        }

        // 1. Reveal background (0 - 150ms)
        launch {
            bgAlpha.animateTo(1f, tween(durationMillis = 150, easing = standardEasing))
        }

        // 2. Waveform bars upward (80 - 280ms)
        launch {
            delay(80)
            waveformProgress.animateTo(1f, tween(durationMillis = 200, easing = decelerationEasing))
        }

        // 3. Play gesture reveal (180 - 450ms)
        launch {
            delay(180)
            playProgress.animateTo(1f, tween(durationMillis = 270, easing = standardEasing))
        }

        // 4. Orbit line & nodes sequence (320 - 640ms)
        launch {
            delay(320)
            orbitProgress.animateTo(1f, tween(durationMillis = 320, easing = standardEasing))
        }
        launch {
            delay(420)
            nodesCount = 1
            delay(90)
            nodesCount = 2
            delay(90)
            nodesCount = 3
        }

        // 5. Wordmark reveal (520 - 740ms)
        launch {
            delay(520)
            launch { wordmarkOffset.animateTo(0f, tween(durationMillis = 220, easing = decelerationEasing)) }
            wordmarkAlpha.animateTo(1f, tween(durationMillis = 220, easing = standardEasing))
        }

        // 6. Tagline reveal (640 - 820ms)
        launch {
            delay(640)
            taglineAlpha.animateTo(1f, tween(durationMillis = 180, easing = standardEasing))
        }

        // 7. Transition to application first screen (~880ms)
        delay(880)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(bgAlpha.value)
            .background(WePlayTheme.colors.canvas),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WePlayMark(
                size = 72.dp,
                tintPlay = WePlayTheme.colors.textPrimary,
                tintAccent = WePlayTheme.colors.accent,
                waveformProgress = waveformProgress.value,
                playProgress = playProgress.value,
                orbitProgress = orbitProgress.value,
                nodesIlluminated = nodesCount
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wordmark: Editorial typography, tight tracking
            Text(
                text = "WePlay",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 32.sp, letterSpacing = (-0.8).sp),
                color = WePlayTheme.colors.textPrimary,
                modifier = Modifier
                    .offset(y = wordmarkOffset.value.dp)
                    .alpha(wordmarkAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline: Truthful, restrained
            Text(
                text = "One source. In sync.",
                style = WePlayTheme.typography.bodyMedium.copy(letterSpacing = 0.3.sp),
                color = WePlayTheme.colors.textSecondary,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}
