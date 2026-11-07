package com.weplay.sync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.ui.components.PlanetaryOrbSoundstage
import com.weplay.sync.ui.components.WePlayBottomNav
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 2: Master Home Screen ("Your Audio Universe Awaits").
 * Exact visual match to Screen 2 in the design reference:
 * - 4-pillar sound mark + "WePlay" + glowing circular user profile avatar
 * - "Your Audio Universe Awaits" headline + "Connect. Broadcast. Share."
 * - PlanetaryOrbSoundstage: molten incandescent orb + concentric orbits + hovering device nodes
 * - Radiant molten orange pill button: "▶ Start Broadcast"
 * - Dark glass pill button: "Join Session"
 * - 4-tab docked bottom navigation (Home active)
 */
@Composable
fun ShareHomeScreen(
    activeSession: AudioSharingSession,
    onStartSession: () -> Unit,
    onJoinSession: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenRooms: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLiveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSessionLive = activeSession.status == CaptureStatus.CAPTURING_ACTIVE

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
                .padding(bottom = 90.dp)
        ) {
            // 1. Top Bar: Brand Mark + WePlay & User Profile Avatar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WePlayMark(size = 28.dp, glowAlpha = 0.5f)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "WePlay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WePlayTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Profile Avatar with Signature Teal Glow Rim
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131D28))
                        .border(1.5.dp, WePlayOrangeGlow.copy(alpha = 0.8f), CircleShape)
                        .clickable { onOpenSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF138073), Color(0xFF21CDB8))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Headline & Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Text(
                    text = "Your Audio\nUniverse Awaits",
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Connect. Broadcast. Share.",
                    fontSize = 14.sp,
                    color = WePlayTextSecondary,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Central Planetary Orb Soundstage Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                PlanetaryOrbSoundstage(size = 310.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Action Buttons (Pill shaped, matching reference)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Action Button: "▶ Start Broadcast" (Radiant Molten Orange Pill)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
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
                            if (isSessionLive) onOpenLiveSession() else onStartSession()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Broadcast",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSessionLive) "View Live Broadcast" else "Start Broadcast",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Action Button: "Join Session" (Dark Glass Pill)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF101520).copy(alpha = 0.85f))
                        .border(1.dp, WePlayBorder, RoundedCornerShape(28.dp))
                        .clickable { onJoinSession() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WePlayIcon(
                            type = WePlayIconType.ROUTE,
                            tint = WePlayTextSecondary,
                            size = 18.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join Session",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = WePlayTextPrimary,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Docked Bottom Navigation Bar
        WePlayBottomNav(
            currentTab = AppScreen.SHARE_HOME,
            onTabSelect = { target ->
                when (target) {
                    AppScreen.LIBRARY -> onOpenLibrary()
                    AppScreen.MULTI_OUTPUT_ROOM -> onOpenRooms()
                    AppScreen.SETTINGS -> onOpenSettings()
                    else -> Unit
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
