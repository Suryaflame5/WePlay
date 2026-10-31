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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.components.WePlayMark
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // 1. Top Navigation & Broadcast State
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

            // ON AIR Pulse Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isMuted) WePlayTheme.colors.separator else Color(0xFF24B9A6).copy(alpha = 0.15f))
                    .border(1.dp, if (isMuted) WePlayTheme.colors.separator else Color(0xFF24B9A6), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color.Gray else Color(0xFF24B9A6))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMuted) "MUTED" else "ON AIR",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                    color = if (isMuted) WePlayTheme.colors.textSecondary else WePlayTheme.colors.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onOpenDiagnostics() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = "STATS ›",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.accent
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Session Title & Source Capture Status
        Text(
            text = session.sessionName,
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp),
            color = WePlayTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Capturing device audio • YouTube, Spotify, video & games",
            style = WePlayTheme.typography.bodyMedium,
            color = WePlayTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Audio Stream QR Code & Session Key (Directly inspired by Google Pixel Auracast QR)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Audio stream QR code",
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "To listen, other people can scan this code with WePlay or enter code",
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // High-Contrast Vector QR Canvas with centered WePlay mark
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WePlayStreamQRCode(sessionCode = session.sessionCode)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Session Code Pill with tap to copy
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WePlayTheme.colors.surfaceSubtle)
                        .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SESSION CODE: ",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                    Text(
                        text = session.sessionCode,
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 14.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📋",
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Connected Listeners Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONNECTED LISTENERS (${session.listeners.size})",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = WePlayTheme.colors.textSecondary
            )
            Text(
                text = "LPCM 48k • 30ms sync",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                color = WePlayTheme.colors.accent
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Connected Listeners List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (session.listeners.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(WePlayTheme.colors.surface)
                            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "WAITING FOR LISTENERS TO JOIN",
                                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = WePlayTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Have companion phones open WePlay on the same Wi-Fi, tap 'Join stream', and enter code ${session.sessionCode} or scan the QR above.",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                color = WePlayTheme.colors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(session.listeners) { listener ->
                    ConnectedListenerCard(
                        listener = listener,
                        onDisconnect = { onRemoveListener(listener.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6. Background Activity Guidance Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(WePlayTheme.colors.surfaceSubtle)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "💡 Switch to YouTube, Spotify, or any media app. WePlay will keep streaming your phone's audio in the background.",
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Host Action Buttons (Mute Broadcast / Stop Session)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mute / Unmute Button
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(WePlayTheme.colors.surface)
                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
                    .clickable { onToggleMute() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMuted) "UNMUTE STREAM" else "MUTE STREAM",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textPrimary
                )
            }

            // Stop Session Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE25241))
                    .clickable { onStopSharing() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "END SHARING SESSION",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 0.8.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ConnectedListenerCard(
    listener: ConnectedListener,
    onDisconnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(WePlayTheme.colors.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (listener.deviceType) {
                        DeviceType.HEADPHONES -> "🎧"
                        DeviceType.SPEAKER -> "🔊"
                        else -> "📱"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listener.name,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${listener.transport.displayName} • +${listener.clockOffsetMs}ms drift • ${listener.rttMs}ms RTT",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onDisconnect() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "DISCONNECT",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFFE25241)
                )
            }
        }
    }
}

@Composable
fun WePlayStreamQRCode(sessionCode: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalCells = 21
        val cellSize = size.width / totalCells

        // Draw clean high-fidelity QR Code matrix with 3 standard finder patterns
        drawRect(color = Color.White, size = size)

        // Draw 3 corner finder patterns
        val cornerPositions = listOf(
            Pair(0, 0),
            Pair(totalCells - 7, 0),
            Pair(0, totalCells - 7)
        )

        cornerPositions.forEach { (cx, cy) ->
            // Outer 7x7
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(cx * cellSize, cy * cellSize),
                size = Size(7 * cellSize, 7 * cellSize),
                cornerRadius = CornerRadius(cellSize, cellSize)
            )
            // Inner 5x5 white
            drawRoundRect(
                color = Color.White,
                topLeft = Offset((cx + 1) * cellSize, (cy + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize),
                cornerRadius = CornerRadius(cellSize * 0.8f, cellSize * 0.8f)
            )
            // Center 3x3 black
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset((cx + 2) * cellSize, (cy + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize),
                cornerRadius = CornerRadius(cellSize * 0.6f, cellSize * 0.6f)
            )
        }

        // Procedural QR data payload dots derived from session code hash
        val hash = sessionCode.hashCode()
        for (r in 0 until totalCells) {
            for (c in 0 until totalCells) {
                val inFinder1 = r < 8 && c < 8
                val inFinder2 = r < 8 && c >= totalCells - 8
                val inFinder3 = r >= totalCells - 8 && c < 8
                val inCenterLogo = r in 8..12 && c in 8..12

                if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenterLogo) {
                    val bit = ((r * 31 + c * 17 + hash) % 3) == 0
                    if (bit) {
                        drawRoundRect(
                            color = Color(0xFF17191E),
                            topLeft = Offset(c * cellSize + cellSize * 0.1f, r * cellSize + cellSize * 0.1f),
                            size = Size(cellSize * 0.8f, cellSize * 0.8f),
                            cornerRadius = CornerRadius(cellSize * 0.25f, cellSize * 0.25f)
                        )
                    }
                }
            }
        }

        // Center branding emblem
        drawCircle(
            color = Color(0xFF24B9A6),
            radius = cellSize * 2.2f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
        drawCircle(
            color = Color.White,
            radius = cellSize * 0.8f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}
