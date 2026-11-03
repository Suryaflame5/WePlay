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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.components.AcousticBusRail
import com.weplay.sync.ui.components.HardwareVectorGlyph
import com.weplay.sync.ui.components.InstrumentChassisCard
import com.weplay.sync.ui.components.WePlayMark
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

@Composable
fun ShareHomeScreen(
    activeSession: AudioSharingSession,
    onStartSession: () -> Unit,
    onJoinSession: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLiveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSessionLive = activeSession.status == CaptureStatus.CAPTURING_ACTIVE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isSessionLive) 100.dp else 28.dp)
        ) {
            // 1. Atelier Top Header & Machine Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WePlayMark(size = 28.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WEPLAY",
                                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp, fontSize = 11.sp),
                                color = AtelierTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AtelierTray)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "HAL 1.0",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                    color = PrecisionCyan
                                )
                            }
                        }
                        Text(
                            text = "ATELIER INSTRUMENTARIUM",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                            color = AtelierTextSecondary
                        )
                    }
                }

                // Quick Navigation Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AtelierTray)
                            .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                            .clickable { onOpenDevices() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "ROUTES",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = AtelierTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AtelierTray)
                            .border(1.dp, AtelierChamfer, CircleShape)
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚙",
                            style = WePlayTheme.typography.bodyMedium,
                            color = AtelierTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Active Acoustic Bus Rail (Reflects live application transmission state)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                AcousticBusRail(
                    isTransmitting = isSessionLive,
                    listenerCount = activeSession.listeners.size,
                    transportFormat = if (isSessionLive) "WADP UDP LPCM 48k/16b • 1500 MTU" else "PIPELINE STANDBY // READY TO ROUTE"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Monolithic Broadcast Transmitter Chassis
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                InstrumentChassisCard(
                    label = "AUDIO BROADCAST TRANSMITTER",
                    statusIndicator = if (isSessionLive) SignalVermilion else AtelierTextSecondary
                ) {
                    Text(
                        text = "Distribute phone audio to multiple headphones and Wi-Fi receivers simultaneously.",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = AtelierTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Machined vermilion trigger capsule
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(SignalVermilion, Color(0xFFD63000))
                                )
                            )
                            .clickable {
                                if (isSessionLive) onOpenLiveSession() else onStartSession()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSessionLive) "OPEN LIVE TRANSMITTER CONSOLE ›" else "START AUDIO BROADCAST",
                                style = WePlayTheme.typography.titleMedium.copy(
                                    fontSize = 13.sp,
                                    letterSpacing = 1.2.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode Selection Specification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AtelierTray)
                                .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CROSS-APP CAPTURE",
                                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                    color = PrecisionCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Android AudioPlaybackCapture via MediaProjection token",
                                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = AtelierTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AtelierTray)
                                .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "STUDIO MASTER",
                                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                    color = CalibratedGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Direct bit-perfect 24-bit/96kHz local FLAC decoding",
                                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = AtelierTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Companion Receiver Chassis
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                InstrumentChassisCard(
                    label = "RECEIVER COMPANION SINK",
                    statusIndicator = ElectricCobalt
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Join an Audio Broadcast",
                                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                color = AtelierTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Scan QR matrix or enter 6-character broadcast PIN to stream in sync.",
                                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = AtelierTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricCobalt)
                                .clickable { onJoinSession() }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "JOIN STREAM",
                                style = WePlayTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Physical Transmission Matrix Telemetry
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryBlock(
                        title = "SYNC ENGINE",
                        value = "< 30ms",
                        caption = "Buffered Alignment",
                        accentColor = PrecisionCyan,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryBlock(
                        title = "WIRE PROTOCOL",
                        value = "WADP v1.0",
                        caption = "Low-Latency UDP",
                        accentColor = ElectricCobalt,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryBlock(
                        title = "AURACAST",
                        value = "READY",
                        caption = "Hardware Probed",
                        accentColor = CalibratedGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Persistent Floating Broadcast Status Capsule
        if (isSessionLive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AtelierGunmetal)
                    .border(1.dp, SignalVermilion, RoundedCornerShape(8.dp))
                    .clickable { onOpenLiveSession() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SignalVermilion)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BROADCAST ACTIVE // ${activeSession.sessionCode}",
                                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                                color = SignalVermilion
                            )
                            Text(
                                text = "${activeSession.listeners.size} listeners bound to clock",
                                style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = AtelierTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "CONSOLE ›",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AtelierTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryBlock(
    title: String,
    value: String,
    caption: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(AtelierTray)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
            color = AtelierTextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = WePlayTheme.typography.tabularMetric.copy(fontSize = 13.sp),
            color = accentColor
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = caption,
            style = WePlayTheme.typography.bodySmall.copy(fontSize = 8.sp),
            color = AtelierTextSecondary
        )
    }
}
