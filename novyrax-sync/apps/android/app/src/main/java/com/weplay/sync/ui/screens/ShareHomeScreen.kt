package com.weplay.sync.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.SharingTransport
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.cos
import kotlin.math.sin

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
            .background(WePlayTheme.colors.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isSessionLive) 96.dp else 24.dp)
        ) {
            // 1. Top Navigation & Identity Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WePlayMark(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WEPLAY",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                        Text(
                            text = "AUDIO SHARING PLATFORM",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, color = WePlayTheme.colors.textSecondary)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // System Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(WePlayTheme.colors.surface)
                            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
                            .clickable { onOpenDevices() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSessionLive) WePlayTheme.colors.accent else Color(0xFF24B9A6))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSessionLive) "ON AIR (${activeSession.listeners.size})" else "ENGINE READY",
                                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = WePlayTheme.colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Settings Trigger
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(WePlayTheme.colors.surface)
                            .border(1.dp, WePlayTheme.colors.separator, CircleShape)
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚙",
                            style = WePlayTheme.typography.bodyMedium,
                            color = WePlayTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Central Editorial Headline
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Share your audio.",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 32.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "One phone. One audio source. Everyone listens through their own Bluetooth headphones or earbuds.",
                    style = WePlayTheme.typography.bodyLarge,
                    color = WePlayTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Signature Radial Listener Cluster Animation (Inspired by EchoTalk & WePlay orbit)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .aspectRatio(1.25f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                RadialAudioSharingCluster(
                    isLive = isSessionLive,
                    listenerCount = if (isSessionLive) activeSession.listeners.size else 3
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Primary Actions (Start a Session vs. Join a Session)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Action: Start Session
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WePlayTheme.colors.textPrimary)
                        .clickable { onStartSession() }
                        .padding(vertical = 16.dp, horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎙",
                            style = WePlayTheme.typography.titleMedium,
                            color = WePlayTheme.colors.canvas
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSessionLive) "VIEW ACTIVE SESSION" else "START A SESSION",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 12.sp, letterSpacing = 1.sp),
                            color = WePlayTheme.colors.canvas
                        )
                    }
                }

                // Secondary Action: Join Session
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WePlayTheme.colors.surface)
                        .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
                        .clickable { onJoinSession() }
                        .padding(vertical = 16.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔗",
                            style = WePlayTheme.typography.titleMedium,
                            color = WePlayTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "JOIN STREAM",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Supported Audio Distribution Transports
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "TRANSMISSION METHODS",
                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                    color = WePlayTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                TransportMethodCard(
                    title = "Android Playback Capture",
                    badge = "Cross-App Audio",
                    description = "Captures eligible audio playing in YouTube, Spotify, VLC, browser videos & games.",
                    icon = "📱",
                    statusText = "SUPPORTED",
                    statusColor = Color(0xFF24B9A6)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TransportMethodCard(
                    title = "WePlay Wi-Fi Receiver Network",
                    badge = "Lossless LPCM",
                    description = "Other people join from their phones and listen via their own Bluetooth headphones.",
                    icon = "📡",
                    statusText = "READY",
                    statusColor = Color(0xFF24B9A6)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TransportMethodCard(
                    title = "Bluetooth Auracast Broadcast",
                    badge = "LE Audio",
                    description = "Direct hardware broadcast for compatible LE Audio hearing aids and earbuds.",
                    icon = "📻",
                    statusText = "HARDWARE DEPENDENT",
                    statusColor = Color(0xFFD0A34A)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Recent Sharing Sessions
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "RECENT SHARING SESSIONS",
                    style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                    color = WePlayTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                RecentSessionRow(
                    name = "Movie Night with Friends",
                    details = "3 listeners • YouTube / Netflix • Yesterday",
                    sessionCode = "WP-9104",
                    onSelect = { onStartSession() }
                )
                Spacer(modifier = Modifier.height(6.dp))
                RecentSessionRow(
                    name = "Gym Audio Pair",
                    details = "2 listeners • Spotify • 3 days ago",
                    sessionCode = "WP-3240",
                    onSelect = { onStartSession() }
                )
            }
        }

        // 7. Persistent Active Session Bar (Floating Capsule)
        if (isSessionLive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF17191E))
                    .border(1.dp, WePlayTheme.colors.accent, RoundedCornerShape(32.dp))
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
                            .background(Color(0xFF24B9A6))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LIVE SHARING SESSION • ON AIR",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, color = Color(0xFF24B9A6))
                        )
                        Text(
                            text = "${activeSession.listeners.size} listeners connected • ${activeSession.sessionCode}",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = Color(0xFFF4F1E9))
                        )
                    }
                    Text(
                        text = "MANAGE ›",
                        style = WePlayTheme.typography.labelSmall.copy(color = Color(0xFF24B9A6))
                    )
                }
            }
        }
    }
}

@Composable
fun RadialAudioSharingCluster(
    isLive: Boolean,
    listenerCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radial")
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Orbit rings and broadcast waves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val ring1 = size.minDimension * 0.28f * (if (isLive) wavePulse else 1f)
            val ring2 = size.minDimension * 0.42f

            // Inner broadcast wave
            drawCircle(
                color = Color(0xFF24B9A6).copy(alpha = if (isLive) 0.18f else 0.08f),
                radius = ring1,
                center = center
            )
            drawCircle(
                color = Color(0xFF24B9A6).copy(alpha = if (isLive) 0.45f else 0.20f),
                radius = ring1,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Outer orbit path
            drawCircle(
                color = Color.Gray.copy(alpha = 0.18f),
                radius = ring2,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Center Host Phone
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(WePlayTheme.colors.textPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱",
                    style = WePlayTheme.typography.titleLarge.copy(fontSize = 24.sp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "HOST",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                color = WePlayTheme.colors.textPrimary
            )
        }

        // Orbiting Listeners
        val listeners = listOf(
            Triple("🎧", "Pixel Buds", 0.0),
            Triple("🎧", "Sony XM5", 120.0),
            Triple("🔊", "Living Room", 240.0)
        )

        Box(modifier = Modifier.rotate(orbitRotation)) {
            listeners.take(listenerCount).forEach { (icon, name, angleDeg) ->
                val rad = Math.toRadians(angleDeg)
                val dist = 115.dp

                Box(
                    modifier = Modifier
                        .rotate(-orbitRotation) // Counter-rotate so icons stay upright
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.surface)
                        .border(1.5.dp, Color(0xFF24B9A6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun TransportMethodCard(
    title: String,
    badge: String,
    description: String,
    icon: String,
    statusText: String,
    statusColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(WePlayTheme.colors.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 18.sp)
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
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(statusColor.copy(alpha = 0.12f))
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
                    text = description,
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun RecentSessionRow(
    name: String,
    details: String,
    sessionCode: String,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
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
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = details,
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
            Text(
                text = sessionCode,
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.accent
            )
        }
    }
}
