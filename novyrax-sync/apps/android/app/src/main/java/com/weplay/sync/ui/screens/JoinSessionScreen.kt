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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.NearbyStream
import com.weplay.sync.shared.model.SharingTransport
import com.weplay.sync.ui.components.HardwareVectorGlyph
import com.weplay.sync.ui.components.TactileConsoleFader
import com.weplay.sync.ui.components.TactileVUMeter
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
            .background(Color(0xFF0A0D12))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                    .background(Color(0xFF131922))
                    .border(1.dp, Color(0xFF263345), RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "‹ ATELIER",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFFCBD5E1)
                )
            }

            Text(
                text = if (isConnectedToHost) "LISTENER SYNCHRONIZED" else "DISCOVER AUDIO STREAM",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                color = if (isConnectedToHost) Color(0xFF00E5C8) else Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isConnectedToHost) {
            // Not connected: Search & Join Flow
            Text(
                text = "Join an audio stream",
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 28.sp, letterSpacing = (-0.6).sp),
                color = Color(0xFFF8FAFC)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Receive lossless synchronized audio broadcast from another phone on your network.",
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Method 1: Architectural QR Scanner Reticle Trigger
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(10.dp))
                    .clickable { onConnectByCode("WP-4829") }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Precision Reticle Box
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF192230)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val w = size.width
                            val h = size.height
                            val arm = 6.dp.toPx()
                            val stroke = 1.5.dp.toPx()
                            val c = Color(0xFF00E5C8)

                            // Top-left
                            drawLine(c, Offset(0f, 0f), Offset(arm, 0f), stroke)
                            drawLine(c, Offset(0f, 0f), Offset(0f, arm), stroke)
                            // Top-right
                            drawLine(c, Offset(w, 0f), Offset(w - arm, 0f), stroke)
                            drawLine(c, Offset(w, 0f), Offset(w, arm), stroke)
                            // Bottom-left
                            drawLine(c, Offset(0f, h), Offset(arm, h), stroke)
                            drawLine(c, Offset(0f, h), Offset(0f, h - arm), stroke)
                            // Bottom-right
                            drawLine(c, Offset(w, h), Offset(w - arm, h), stroke)
                            drawLine(c, Offset(w, h), Offset(w, h - arm), stroke)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan Host QR Code",
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Point camera at host's WePlay screen for instant pairing",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF003830))
                            .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SCAN",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color(0xFF00E5C8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Method 2: Manual 6-Character Session PIN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "MANUAL SESSION PIN",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = enteredCode,
                            onValueChange = { if (it.length <= 7) enteredCode = it.uppercase() },
                            placeholder = { Text("WP-4829", style = WePlayTheme.typography.tabularMetric.copy(color = Color(0xFF475569))) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (enteredCode.length >= 4) Color(0xFF00E5C8) else Color(0xFF1E293B)
                                )
                                .clickable(enabled = enteredCode.length >= 4) {
                                    onConnectByCode(enteredCode)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "CONNECT",
                                style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (enteredCode.length >= 4) Color(0xFF0A0D12) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nearby Audio Streams
            Text(
                text = "NEARBY ACTIVE BROADCASTS",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                color = Color(0xFF64748B)
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
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF111722))
                                .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCANNING LOCAL WI-FI FREQUENCIES",
                                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 9.sp),
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ensure the host phone has tapped 'Start Broadcast' on the same Wi-Fi subnet. Or enter the 6-character PIN above.",
                                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                    color = Color(0xFF94A3B8),
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
                style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp, letterSpacing = (-0.6).sp),
                color = Color(0xFFF8FAFC)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Connected to ${connectedHostName ?: "Host"} • ${connectedSessionCode ?: "WP-4829"}",
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = Color(0xFF00E5C8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Receiver Output Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE LOCAL OUTPUT SINK",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 9.sp),
                            color = Color(0xFF64748B)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF003830))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LPCM 48kHz SYNCHRONIZED",
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                color = Color(0xFF00E5C8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF192230)),
                            contentAlignment = Alignment.Center
                        ) {
                            HardwareVectorGlyph(type = DeviceType.HEADPHONES, tint = Color(0xFF00E5C8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sony WF-1000XM5",
                                style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = Color(0xFFF1F5F9)
                            )
                            Text(
                                text = "Bluetooth A2DP • Low-Latency AudioTrack Ring Buffer",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TactileVUMeter(level = 0.70f, isPlaying = true)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Listener Fader
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                TactileConsoleFader(
                    value = localVolume,
                    onValueChange = { localVolume = it },
                    label = "PERSONAL LISTENER ATTENUATION"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lip-Sync Video Alignment Calibration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "LIP-SYNC VIDEO ALIGNMENT",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 9.sp),
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Offset audio timing to match host video screen exactly",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                        color = Color(0xFF94A3B8)
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
                                    .background(if (isSel) Color(0xFF00E5C8) else Color(0xFF192230))
                                    .border(1.dp, if (isSel) Color(0xFF00E5C8) else Color(0xFF263345), RoundedCornerShape(4.dp))
                                    .clickable { syncOffsetMs = ms }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${ms}ms",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                                    color = if (isSel) Color(0xFF0A0D12) else Color(0xFFCBD5E1)
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
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF453A), Color(0xFFD63031))
                        )
                    )
                    .clickable { onLeaveSession() }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DISCONNECT FROM STREAM",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 0.8.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
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
            .background(Color(0xFF141922))
            .border(1.dp, Color(0xFF222B38), RoundedCornerShape(8.dp))
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
                    .background(Color(0xFF1E2838)),
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
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${stream.hostName} • ${stream.transport.badge} • ${stream.listenerCount} listening",
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = Color(0xFF777D86)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF00E5C8))
                    .clickable { onConnect() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "LISTEN",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFF0B1017)
                )
            }
        }
    }
}
