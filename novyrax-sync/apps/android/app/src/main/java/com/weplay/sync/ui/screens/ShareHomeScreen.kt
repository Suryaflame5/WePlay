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
import com.weplay.sync.ui.components.HardwareVectorGlyph
import com.weplay.sync.ui.components.SpatialAcousticSoundstage
import com.weplay.sync.ui.components.TactileVUMeter
import com.weplay.sync.ui.components.WePlayMark
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
            .background(Color(0xFF0A0D12)) // Deep Architectural Studio Charcoal
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isSessionLive) 100.dp else 28.dp)
        ) {
            // 1. Studio Header & Chassis Bar
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
                                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 2.sp, fontSize = 11.sp),
                                color = Color(0xFFF1F5F9)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "v2.4",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Text(
                            text = "MULTI-DEVICE AUDIO DISTRIBUTION",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp, color = Color(0xFF64748B))
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Engine State Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSessionLive) Color(0xFF003830) else Color(0xFF131922))
                            .border(1.dp, if (isSessionLive) Color(0xFF00E5C8) else Color(0xFF263345), RoundedCornerShape(4.dp))
                            .clickable { onOpenDevices() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSessionLive) Color(0xFF00E5C8) else Color(0xFF38BDF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSessionLive) "ON AIR (${activeSession.listeners.size})" else "ENGINE READY",
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                                color = if (isSessionLive) Color(0xFF00E5C8) else Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tactical Settings Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF131922))
                            .border(1.dp, Color(0xFF263345), RoundedCornerShape(6.dp))
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚙", fontSize = 14.sp, color = Color(0xFFCBD5E1))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Editorial Lead Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Acoustic Stage",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 32.sp, letterSpacing = (-0.8).sp),
                    color = Color(0xFFF8FAFC)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Broadcast your phone's real-time audio across unlimited Bluetooth headphones and synchronized Wi-Fi receivers.",
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Central Interactive Spatial Soundstage
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                SpatialAcousticSoundstage(
                    hostName = "My Host Audio Stream",
                    listeners = activeSession.listeners,
                    isLive = isSessionLive
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Primary Studio Switchgear (Broadcast & Receive)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // START BROADCAST BUTTON (Machined Aluminum / Teal Tone)
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF00E5C8), Color(0xFF00A896))
                            )
                        )
                        .clickable { onStartSession() }
                        .padding(vertical = 15.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSessionLive) "VIEW LIVE BROADCAST" else "START AUDIO BROADCAST",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 1.1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = Color(0xFF0A0D12)
                        )
                    }
                }

                // JOIN STREAM BUTTON
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF131922))
                        .border(1.dp, Color(0xFF263345), RoundedCornerShape(8.dp))
                        .clickable { onJoinSession() }
                        .padding(vertical = 15.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📡", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "JOIN STREAM",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 0.8.sp),
                            color = Color(0xFFF1F5F9)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Active Transmission Matrix Rack
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSMISSION CHANNELS",
                        style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 9.sp),
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "WADP v1.0 • 48kHz LPCM",
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                        color = Color(0xFF00E5C8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transport 1: Playback Capture
                ChassisChannelCard(
                    title = "Android System Audio Capture",
                    badge = "Cross-App HAL",
                    description = "Intercepts eligible media from YouTube, Spotify, VLC, podcast players & games.",
                    type = DeviceType.SYSTEM_BUILTIN,
                    statusText = "SUPPORTED",
                    statusColor = Color(0xFF00E5C8),
                    level = if (isSessionLive) 0.72f else 0f,
                    isPlaying = isSessionLive
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Transport 2: Lossless Wi-Fi Network
                ChassisChannelCard(
                    title = "WePlay UDP Multi-Receiver Engine",
                    badge = "Lossless 1,536 kbps",
                    description = "Distributes raw 16-bit 48kHz LPCM slices directly over local Wi-Fi with sequence ordering.",
                    type = DeviceType.NETWORK_RECEIVER,
                    statusText = "READY",
                    statusColor = Color(0xFF00E5C8),
                    level = if (isSessionLive) 0.65f else 0f,
                    isPlaying = isSessionLive
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Transport 3: Auracast Broadcast
                ChassisChannelCard(
                    title = "Bluetooth Auracast Assistant",
                    badge = "LE Audio LC3",
                    description = "Assists system-level hardware broadcasting to compatible hearing aids and earbuds.",
                    type = DeviceType.HI_FI_SYSTEM,
                    statusText = "PROBE CAPABLE",
                    statusColor = Color(0xFFFF9500),
                    level = 0f,
                    isPlaying = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Recent Soundstages & Rooms
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "RECENT AUDIO SESSIONS",
                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontSize = 9.sp),
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(10.dp))

                StudioSessionRow(
                    name = "Late Night Movie Sync",
                    meta = "3 listeners • YouTube / Netflix • 24ms avg drift",
                    code = "WP-9104",
                    onSelect = onStartSession
                )
                Spacer(modifier = Modifier.height(6.dp))
                StudioSessionRow(
                    name = "Gym Twin-Earbud Pair",
                    meta = "2 listeners • Spotify • Lossless LPCM",
                    code = "WP-3240",
                    onSelect = onStartSession
                )
            }
        }

        // 7. Persistent Broadcast Monitor Capsule
        if (isSessionLive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF111722))
                    .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(32.dp))
                    .clickable { onOpenLiveSession() }
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5C8))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LIVE SHARING SESSION • ON AIR",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color(0xFF00E5C8))
                        )
                        Text(
                            text = "${activeSession.listeners.size} connected & receiving • ${activeSession.sessionCode}",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = Color(0xFFF1F5F9))
                        )
                    }
                    Text(
                        text = "STUDIO RACK ›",
                        style = WePlayTheme.typography.labelSmall.copy(color = Color(0xFF00E5C8), fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChassisChannelCard(
    title: String,
    badge: String,
    description: String,
    type: DeviceType,
    statusText: String,
    statusColor: Color,
    level: Float,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF111722))
            .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF192230)),
                    contentAlignment = Alignment.Center
                ) {
                    HardwareVectorGlyph(type = type, tint = statusColor)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = Color(0xFFF1F5F9)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusText,
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                color = statusColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = badge,
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color(0xFF64748B))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
                color = Color(0xFF94A3B8)
            )

            if (isPlaying) {
                Spacer(modifier = Modifier.height(10.dp))
                TactileVUMeter(level = level, isPlaying = true)
            }
        }
    }
}

@Composable
fun StudioSessionRow(
    name: String,
    meta: String,
    code: String,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF111722))
            .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(6.dp))
            .clickable { onSelect() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = name,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = meta,
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    color = Color(0xFF64748B)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF192230))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = code,
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = Color(0xFF00E5C8)
                )
            }
        }
    }
}
