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
import com.weplay.sync.ui.components.HiFiVolumeSlider
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
    var syncOffsetMs by remember { mutableStateOf(0) } // Lip-sync adjustment

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }

            Text(
                text = if (isConnectedToHost) "LISTENER ACTIVE" else "FIND AUDIO STREAM",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = if (isConnectedToHost) Color(0xFF24B9A6) else WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isConnectedToHost) {
            // Not connected: Search & Join Flow
            Text(
                text = "Join an audio stream",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp),
                color = WePlayTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Listen to another phone that's sharing audio or to a nearby Auracast broadcast.",
                style = WePlayTheme.typography.bodyMedium,
                color = WePlayTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: Scan QR Code Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(10.dp))
                    .clickable { onConnectByCode("WP-4829") }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WePlayTheme.colors.surfaceSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📷", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan Host QR Code",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Point camera at host's WePlay screen to sync instantly",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                    }
                    Text(
                        text = "SCAN ›",
                        style = WePlayTheme.typography.labelSmall,
                        color = Color(0xFF24B9A6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option 2: Enter Session Code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "OR ENTER 6-CHARACTER CODE",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = enteredCode,
                            onValueChange = { if (it.length <= 7) enteredCode = it.uppercase() },
                            placeholder = { Text("e.g. WP-4829", style = WePlayTheme.typography.bodyMedium) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (enteredCode.length >= 4) WePlayTheme.colors.textPrimary else WePlayTheme.colors.separator)
                                .clickable(enabled = enteredCode.length >= 4) {
                                    onConnectByCode(enteredCode)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "CONNECT",
                                style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = WePlayTheme.colors.canvas
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nearby Audio Streams (Directly inspired by Google Pixel Auracast list)
            Text(
                text = "NEARBY AUDIO STREAMS",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = WePlayTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (nearbyStreams.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(WePlayTheme.colors.surface)
                                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCANNING FOR LOCAL BROADCASTS",
                                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    color = WePlayTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ensure the host device has started sharing on the same Wi-Fi network. Or enter the 6-character session PIN above.",
                                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                    color = WePlayTheme.colors.textSecondary,
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
                text = "Listening to audio stream",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp),
                color = WePlayTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Connected to ${connectedHostName ?: "Host"} • ${connectedSessionCode ?: "WP-4829"}",
                style = WePlayTheme.typography.bodyMedium,
                color = Color(0xFF24B9A6)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Receiver Output Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR LOCAL AUDIO ROUTE",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF24B9A6).copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SYNCHRONIZED",
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                color = Color(0xFF24B9A6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(WePlayTheme.colors.surfaceSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎧", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sony WF-1000XM5",
                                style = WePlayTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                color = WePlayTheme.colors.textPrimary
                            )
                            Text(
                                text = "Bluetooth A2DP • 48kHz LPCM via WePlay Wi-Fi Client",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = WePlayTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Personal Listener Volume
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LISTENER VOLUME",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                        Text(
                            text = "${(localVolume * 100).toInt()}%",
                            style = WePlayTheme.typography.tabularMetric.copy(fontSize = 12.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HiFiVolumeSlider(
                        value = localVolume,
                        onValueChange = { localVolume = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Lip-Sync Video Alignment Calibration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "LIP-SYNC VIDEO ALIGNMENT",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adjust audio timing to align with host's video screen",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary
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
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) WePlayTheme.colors.textPrimary else WePlayTheme.colors.surfaceSubtle)
                                    .clickable { syncOffsetMs = ms }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${ms}ms",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                                    color = if (isSel) WePlayTheme.colors.canvas else WePlayTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Leave Stream Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE25241))
                    .clickable { onLeaveSession() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LEAVE AUDIO STREAM",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 12.sp, letterSpacing = 0.8.sp),
                    color = Color.White
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
            .clip(RoundedCornerShape(8.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(WePlayTheme.colors.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (stream.transport == SharingTransport.BLUETOOTH_AURACAST) "📻" else "📡",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.streamName,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${stream.hostName} • ${stream.transport.badge} • ${stream.listenerCount} listening",
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(WePlayTheme.colors.textPrimary)
                    .clickable { onConnect() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "LISTEN",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = WePlayTheme.colors.canvas
                )
            }
        }
    }
}
