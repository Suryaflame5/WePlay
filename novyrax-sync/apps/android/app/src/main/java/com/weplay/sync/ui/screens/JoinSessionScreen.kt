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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.NearbyStream
import com.weplay.sync.shared.model.SharingTransport
import com.weplay.sync.ui.components.InstrumentChassisCard
import com.weplay.sync.ui.components.TactileConsoleFader
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
fun JoinSessionScreen(
    isConnectedToHost: Boolean,
    connectedHostName: String?,
    connectedSessionCode: String?,
    nearbyStreams: List<NearbyStream>,
    onConnectByCode: (String) -> Unit,
    onConnectToStream: (NearbyStream) -> Unit,
    onLeaveSession: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredCode by remember { mutableStateOf("") }
    var localVolume by remember { mutableFloatStateOf(0.85f) }
    var syncOffsetMs by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // 1. Navigation Header
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
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = AtelierTextPrimary
                )
            }

            Text(
                text = if (isConnectedToHost) "LISTENER ACTIVE // SYNC LOCKED" else "RECEIVER EXPLORATION CONSOLE",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = if (isConnectedToHost) PrecisionCyan else ElectricCobalt
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isConnectedToHost) {
            // Not connected: Search & Join Flow
            Text(
                text = "Join an Audio Stream",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
                color = AtelierTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Listen to an audio broadcast from another phone using your headphones.",
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = AtelierTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Optical Reticle QR Camera Trigger Chassis
            InstrumentChassisCard(
                label = "OPTICAL MATRIX SCANNER",
                statusIndicator = PrecisionCyan
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AtelierTray)
                        .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
                        .clickable {
                            if (nearbyStreams.isNotEmpty()) onConnectToStream(nearbyStreams.first())
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AtelierGunmetal)
                            .border(1.dp, PrecisionCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⛶", fontSize = 18.sp, color = PrecisionCyan)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan Host Broadcast Matrix",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "Point camera at host screen for zero-touch pairing",
                            style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = AtelierTextSecondary
                        )
                    }

                    Text(
                        text = "SCAN ›",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = PrecisionCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Manual PIN Input Chassis
            InstrumentChassisCard(
                label = "MANUAL SESSION PIN",
                statusIndicator = ElectricCobalt
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = { if (it.length <= 6) enteredCode = it.uppercase() },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "e.g. WP-4829",
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 13.sp),
                                color = AtelierTextSecondary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (enteredCode.length >= 4) SignalVermilion else AtelierTray)
                            .border(1.dp, if (enteredCode.length >= 4) SignalVermilion else AtelierChamfer, RoundedCornerShape(4.dp))
                            .clickable(enabled = enteredCode.length >= 4) {
                                onConnectByCode(enteredCode)
                            }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JOIN",
                            style = WePlayTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = if (enteredCode.length >= 4) Color.White else AtelierTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Nearby Streams Roster
            Text(
                text = "NEARBY BROADCASTS DETECTED",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = AtelierTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (nearbyStreams.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AtelierGunmetal)
                                .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCANNING LOCAL WI-FI SUBNET",
                                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 9.sp),
                                    color = AtelierTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ensure the host phone has tapped 'Start Audio Broadcast' on the same network.",
                                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = AtelierTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(nearbyStreams) { stream ->
                        NearbyStreamCard(
                            stream = stream,
                            onConnect = { onConnectToStream(stream) }
                        )
                    }
                }
            }
        } else {
            // Connected Listener Session Interface
            Text(
                text = "Streaming Audio In Sync",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
                color = AtelierTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Connected to ${connectedHostName ?: "Host"} // Session ${connectedSessionCode ?: "WP-4829"}",
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = PrecisionCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Receiver Output Status Chassis
            InstrumentChassisCard(
                label = "LOCAL RECEIVER STATUS",
                statusIndicator = PrecisionCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AtelierTray)
                            .border(1.dp, PrecisionCyan, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎧", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Playback Route: Bluetooth Headphones",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "Direct hardware output • Low-latency sync buffer: 28ms",
                            style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = AtelierTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Local Listener Console Fader
            InstrumentChassisCard(
                label = "PERSONAL ATTENUATION",
                statusIndicator = SignalVermilion
            ) {
                TactileConsoleFader(
                    value = localVolume,
                    onValueChange = { localVolume = it },
                    label = "LOCAL EARBUD GAIN"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lip-Sync Video Delay Alignment
            InstrumentChassisCard(
                label = "VIDEO LIP-SYNC ALIGNMENT",
                statusIndicator = ElectricCobalt
            ) {
                Text(
                    text = "Calibrate audio delay (+ms) if watching a video on the host screen",
                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = AtelierTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0, 15, 30, 45).forEach { ms ->
                        val isSel = syncOffsetMs == ms
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) SignalVermilion else AtelierTray)
                                .border(1.dp, if (isSel) SignalVermilion else AtelierChamfer, RoundedCornerShape(4.dp))
                                .clickable { syncOffsetMs = ms }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${ms}ms",
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                                color = if (isSel) Color.White else AtelierTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Disconnect Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF3B30))
                    .clickable { onLeaveSession() }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DISCONNECT FROM BROADCAST",
                    style = WePlayTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun NearbyStreamCard(
    stream: NearbyStream,
    onConnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AtelierGunmetal)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (stream.transport == SharingTransport.BLUETOOTH_AURACAST) "📻" else "📡",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.streamName,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = AtelierTextPrimary
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${stream.hostName} • ${stream.transport.badge} • ${stream.listenerCount} listening",
                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = AtelierTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SignalVermilion)
                    .clickable { onConnect() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "LISTEN",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                    color = Color.White
                )
            }
        }
    }
}
