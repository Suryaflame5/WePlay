package com.weplay.sync.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.model.SyncModeSetting
import com.weplay.sync.ui.components.FlowingCopperRibbon
import com.weplay.sync.ui.components.PenthouseSunsetOrb
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 10: Master Onboarding Screen ("A Shared Soundscape Across Every Device").
 * Exact visual match to Screen 10 in the design reference:
 * - Top half: Penthouse sunset interior with floating glowing acoustic sphere radiating concentric circular wavefronts
 * - Bottom half:
 *     - "WePlay" headline
 *     - "A Shared Soundscape Across Every Device" subtitle
 *     - Wide radiant molten orange pill button: "Next >>"
 *     - Flowing copper metallic ribbon filament along the bottom edge
 */
@Composable
fun OnboardingScreen(
    onComplete: (quality: AudioQualitySetting, sync: SyncModeSetting) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Penthouse Sunset Living Room with Floating Glowing Acoustic Orb
            PenthouseSunsetOrb(
                height = 300.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Title & Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WePlay",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share Music with Two Headphones\nListen together with friends wirelessly",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = WePlayTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Primary Action Button: "Start Listening →" (Signature Electric Teal Pill)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                WePlayOrangePrimary,
                                WePlayOrangeGlow
                            )
                        )
                    )
                    .clickable {
                        onComplete(AudioQualitySetting.SOURCE_PRESERVING, SyncModeSetting.TIGHT_ALIGNMENT)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Start Listening",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Flowing Copper Ribbon Filament at bottom
        FlowingCopperRibbon(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(0.65f)
                .navigationBarsPadding(),
            height = 100.dp
        )
    }
}
