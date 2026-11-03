package com.weplay.sync.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.components.FlowingCopperRibbon
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 1: Master Splash Screen.
 * Exact visual match to Screen 1 in reference:
 * - Pure obsidian background
 * - Glowing 4-pillar amber sound mark
 * - "WePlay" title + "Share Sound. Together."
 * - "Powered by Auracast™" footer
 * - Flowing copper ribbon wave in background
 */
@Composable
fun WePlaySplashScreen(
    isReady: Boolean = true,
    reduceMotion: Boolean = false,
    onAnimationComplete: () -> Unit
) {
    val markScale = remember { Animatable(0.7f) }
    val markAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(16f) }
    val footerAlpha = remember { Animatable(0f) }

    val smoothEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    LaunchedEffect(isReady) {
        if (!isReady) return@LaunchedEffect

        if (reduceMotion) {
            markScale.snapTo(1f)
            markAlpha.snapTo(1f)
            textAlpha.snapTo(1f)
            textOffsetY.snapTo(0f)
            footerAlpha.snapTo(1f)
            delay(400)
            onAnimationComplete()
            return@LaunchedEffect
        }

        // 1. Reveal glowing 4-pillar mark
        launch {
            markAlpha.animateTo(1f, tween(400, easing = smoothEasing))
        }
        launch {
            markScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }

        // 2. Reveal "WePlay" + "Share Sound. Together."
        launch {
            delay(250)
            textAlpha.animateTo(1f, tween(450, easing = smoothEasing))
        }
        launch {
            delay(250)
            textOffsetY.animateTo(0f, tween(450, easing = smoothEasing))
        }

        // 3. Reveal Auracast footer
        launch {
            delay(400)
            footerAlpha.animateTo(1f, tween(350, easing = smoothEasing))
        }

        delay(1200)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayObsidian),
        contentAlignment = Alignment.Center
    ) {
        // Background Flowing Copper Ribbon Filament
        FlowingCopperRibbon(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(0.85f),
            height = 220.dp
        )

        // Center Brand Identity
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing 4-Pillar Mark
            Box(
                modifier = Modifier
                    .alpha(markAlpha.value)
                    .offset(y = (20 * (1f - markScale.value)).dp)
            ) {
                WePlayMark(
                    size = 64.dp,
                    glowAlpha = 0.65f
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share Sound. Together.",
                    color = WePlayTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Footer: Powered by Auracast™
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(footerAlpha.value),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WePlayIcon(
                type = WePlayIconType.AURACAST,
                tint = WePlayTextMuted,
                size = 16.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Powered by Auracast™",
                color = WePlayTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp
            )
        }
    }
}
