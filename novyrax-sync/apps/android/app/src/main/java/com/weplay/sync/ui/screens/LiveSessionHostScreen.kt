package com.weplay.sync.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.ui.components.AcousticBusRail
import com.weplay.sync.ui.components.HardwareVectorGlyph
import com.weplay.sync.ui.components.InstrumentChassisCard
import com.weplay.sync.ui.components.ModularSinkPod
import com.weplay.sync.ui.components.TactileConsoleFader
import com.weplay.sync.ui.components.TactileVUMeter
import com.weplay.sync.ui.components.WePlayStreamQRCode
import com.weplay.sync.ui.theme.AtelierChamfer
import com.weplay.sync.ui.theme.AtelierGunmetal
import com.weplay.sync.ui.theme.AtelierObsidian
import com.weplay.sync.ui.theme.AtelierTextPrimary
import com.weplay.sync.ui.theme.AtelierTextSecondary
import com.weplay.sync.ui.theme.AtelierTray
import com.weplay.sync.ui.theme.ElectricCobalt
import com.weplay.sync.ui.theme.PrecisionCyan
import com.weplay.sync.ui.theme.SignalVermilion
import com.weplay.sync.ui.theme.WePlayTheme

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
    val isMuted = session.status == CaptureStatus.MUTED
    var masterVolume by remember { mutableFloatStateOf(0.90f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // 1. Studio Rack Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "‹ CONSOLE",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = AtelierTextPrimary
                )
            }

            // High-Visibility Signal Vermilion Live Status Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isMuted) AtelierTray else Color(0xFF330B00))
                    .border(1.dp, if (isMuted) AtelierChamfer else SignalVermilion, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) AtelierTextSecondary else SignalVermilion)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMuted) "BUS MUTED" else "ON AIR // BROADCASTING",
                    style = WePlayTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = if (isMuted) AtelierTextSecondary else SignalVermilion
                    )
                )
            }

            // Stop Broadcast Trigger
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray)
                    .border(1.dp, Color(0xFFFF453A), RoundedCornerShape(4.dp))
                    .clickable { onStopSharing() }
                    .padding(vertical = 5.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "STOP",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFFFF453A)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Active Acoustic Bus Rail
        AcousticBusRail(
            isTransmitting = !isMuted,
            listenerCount = session.listeners.size,
            transportFormat = "WADP v1.0 • 48kHz LPCM • ZERO-COPY HAL"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. QR Matrix Invitation Aperture
        InstrumentChassisCard(
            label = "PAIRING MATRIX & ACCESS KEY",
            statusIndicator = PrecisionCyan
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High-Contrast Vector QR Matrix
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WePlayStreamQRCode(sessionCode = session.sessionCode)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SESSION ACCESS PIN",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                        color = AtelierTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.sessionCode,
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 20.sp),
                        color = PrecisionCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Listeners scan the matrix or input this PIN on their device to bind into playback.",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                        color = AtelierTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Master Bus Console Fader & Stereo Peak VU
        InstrumentChassisCard(
            label = "MASTER BUS GAIN & TELEMETRY",
            statusIndicator = SignalVermilion
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TactileConsoleFader(
                    value = if (isMuted) 0f else masterVolume,
                    onValueChange = { masterVolume = it },
                    label = "MASTER CONSOLE ATTENUATION",
                    modifier = Modifier.weight(1f),
                    enabled = !isMuted
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AtelierTray)
                        .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp))
                        .clickable { onToggleMute() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMuted) "UNMUTE" else "MUTE BUS",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = if (isMuted) SignalVermilion else AtelierTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stereo Progressive Peak VU Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "L/R",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = AtelierTextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                TactileVUMeter(
                    level = if (isMuted) 0f else masterVolume,
                    isPlaying = !isMuted,
                    barCount = 18,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Connected Sinks Module Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${session.listeners.size} CONNECTED SINKS IN SYNC",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = ElectricCobalt
            )

            Box(
                modifier = Modifier
                    .clickable { onOpenDiagnostics() }
                    .padding(4.dp)
            ) {
                Text(
                    text = "VIEW JITTER STATS ›",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = PrecisionCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 6. Connected Listener Modular Sinks List
        if (session.listeners.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AtelierGunmetal)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "WAITING FOR RECEIVERS TO CONNECT",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                        color = AtelierTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Have companion phones open WePlay and scan the pairing matrix above.",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = AtelierTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(session.listeners, key = { it.id }) { listener ->
                    ModularSinkPod(
                        listener = listener,
                        volume = 0.85f,
                        onVolumeChange = { /* Remote volume adjustment */ },
                        onDisconnect = { onRemoveListener(listener.id) }
                    )
                }
            }
        }
    }
}
