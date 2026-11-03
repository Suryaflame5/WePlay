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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.ui.components.IsometricSpectrumCube
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayStreamQRCode
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayStatusGreen
import com.weplay.sync.ui.theme.WePlayStatusRed
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import kotlinx.coroutines.delay

/**
 * Screen 3: Master Active Broadcast Screen ("Broadcasting...").
 * Exact visual match to Screen 3 in the design reference:
 * - Top Bar: "● LIVE" pill badge + "00:12:48" timer
 * - Isometric 3D glass cube with animated amber frequency spectrum bars inside
 * - Status header: "Broadcasting..." + "Audio is being shared to 3 devices"
 * - Split card: Monospace Session PIN ("WP-4829") + High-contrast QR Code
 * - Horizontal connected device pods: AirPods, Studio Pro, Living Room
 * - Full-width dark glass pill: "■ End Broadcast"
 */
@Composable
fun LiveSessionHostScreen(
    session: AudioSharingSession,
    onToggleMute: () -> Unit,
    onStopSharing: () -> Unit,
    onRemoveListener: (String) -> Unit,
    onOpenDiagnostics: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Simulated timer for broadcasting elapsed time
    var secondsElapsed by remember { mutableIntStateOf(768) } // 12m 48s as in reference
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val hours = secondsElapsed / 3600
    val minutes = (secondsElapsed % 3600) / 60
    val secs = secondsElapsed % 60
    val formattedTimer = String.format("%02d:%02d:%02d", hours, minutes, secs)

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
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: "● LIVE" Badge & Elapsed Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red LIVE pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(WePlayStatusRed.copy(alpha = 0.2f))
                        .border(1.dp, WePlayStatusRed.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(WePlayStatusRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WePlayStatusRed,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Timer Readout (Monospace)
                Text(
                    text = formattedTimer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = WePlayTextPrimary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Isometric Spectrum Glass Cube Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                IsometricSpectrumCube(size = 250.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Broadcasting Status Typography
            Text(
                text = "Broadcasting...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = WePlayTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Audio is being shared to 3 devices",
                fontSize = 13.sp,
                color = WePlayTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Session Info Split Card (PIN on Left, QR Code on Right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlaySurface)
                    .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left half: PIN
                    Column {
                        Text(
                            text = "Session PIN",
                            fontSize = 12.sp,
                            color = WePlayTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = session.sessionCode.ifEmpty { "WP-4829" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = WePlayTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    // Right half: High-contrast QR Code
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WePlayStreamQRCode(sessionCode = session.sessionCode.ifEmpty { "WP-4829" })
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Horizontal Connected Devices Row (AirPods, Studio Pro, Living Room)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val mockDevices = listOf(
                    Triple("AirPods", WePlayIconType.HEADPHONES, true),
                    Triple("Studio Pro", WePlayIconType.SPEAKER, true),
                    Triple("Living Room", WePlayIconType.ROOM, true)
                )

                mockDevices.forEach { (name, iconType, isConnected) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(WePlaySurface)
                            .border(1.dp, WePlayBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WePlaySurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                WePlayIcon(
                                    type = iconType,
                                    tint = WePlayTextPrimary,
                                    size = 18.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WePlayTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Connected",
                                fontSize = 10.sp,
                                color = WePlayStatusGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 6. Action Button: "■ End Broadcast" (Full-width dark glass pill)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF141A26))
                    .border(1.dp, WePlayBorder, RoundedCornerShape(26.dp))
                    .clickable { onStopSharing() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(WePlayStatusRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "End Broadcast",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WePlayTextPrimary
                    )
                }
            }
        }
    }
}
