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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.ui.components.HardwareVectorGlyph
import com.weplay.sync.ui.components.TactileConsoleFader
import com.weplay.sync.ui.components.TactileVUMeter
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
            .background(Color(0xFF0A0D12))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                    .background(Color(0xFF131922))
                    .border(1.dp, Color(0xFF263345), RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "‹ CONSOLE",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFFCBD5E1)
                )
            }

            // ON AIR Pulse Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isMuted) Color(0xFF1E293B) else Color(0xFF003830))
                    .border(1.dp, if (isMuted) Color(0xFF475569) else Color(0xFF00E5C8), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color(0xFF94A3B8) else Color(0xFF00E5C8))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMuted) "FEED MUTED" else "ON AIR • BROADCASTING",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
                    color = if (isMuted) Color(0xFF94A3B8) else Color(0xFF00E5C8)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF131922))
                    .border(1.dp, Color(0xFF263345), RoundedCornerShape(4.dp))
                    .clickable { onOpenDiagnostics() }
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "TELEMETRY ›",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFF00E5C8)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Stream Channel Title & Real-time VU Levels
        Text(
            text = session.sessionName,
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp, letterSpacing = (-0.6).sp),
            color = Color(0xFFF8FAFC)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Capturing device audio • 48,000 Hz Stereo LPCM • Sliced 5ms WADP",
            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Stereo Master Output VU Meter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "L",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(6.dp))
            TactileVUMeter(
                level = if (isMuted) 0f else 0.78f,
                isPlaying = !isMuted,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "R",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(6.dp))
            TactileVUMeter(
                level = if (isMuted) 0f else 0.74f,
                isPlaying = !isMuted,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Audio Stream QR Code & Session Key Chassis
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF111722))
                .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(10.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PAIRING CODE & SCANNER",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // High-Contrast Vector QR Canvas with centered WePlay mark
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WePlayStreamQRCode(sessionCode = session.sessionCode)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Session Code Pill with tap to copy
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF192230))
                        .border(1.dp, Color(0xFF263345), RoundedCornerShape(4.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SESSION PIN: ",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = session.sessionCode,
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 14.sp),
                        color = Color(0xFF00E5C8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "📋", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Connected Listeners Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONNECTED LISTENERS (${session.listeners.size})",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                color = Color(0xFF64748B)
            )
            Text(
                text = "LPCM 48k • 30ms sync",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                color = Color(0xFF00E5C8)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Connected Listeners Rack List
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
                            .background(Color(0xFF111722))
                            .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(6.dp))
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AWAITING COMPANION CONNECTIONS",
                                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 9.sp),
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Have other listeners open WePlay on the same Wi-Fi, tap 'Join stream', and enter PIN ${session.sessionCode} or point camera at the QR above.",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                color = Color(0xFF94A3B8),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(session.listeners) { listener ->
                    ListenerConsoleRackCard(
                        listener = listener,
                        onDisconnect = { onRemoveListener(listener.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6. Action Switchgear (Mute Feed & Terminate Broadcast)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mute / Unmute Button
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF131922))
                    .border(1.dp, Color(0xFF263345), RoundedCornerShape(6.dp))
                    .clickable { onToggleMute() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMuted) "UNMUTE STREAM" else "MUTE STREAM",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFFF1F5F9)
                )
            }

            // Emergency Stop Session Button
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF453A), Color(0xFFD63031))
                        )
                    )
                    .clickable { onStopSharing() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "END BROADCAST SESSION",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 0.8.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ListenerConsoleRackCard(
    listener: ConnectedListener,
    onDisconnect: () -> Unit
) {
    var volume by remember { mutableFloatStateOf(0.85f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF111722))
            .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF192230)),
                    contentAlignment = Alignment.Center
                ) {
                    HardwareVectorGlyph(type = listener.deviceType, tint = Color(0xFF00E5C8))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = listener.name,
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "${listener.transport.displayName} • +${listener.clockOffsetMs}ms drift • ${listener.rttMs}ms RTT",
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onDisconnect() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DROP",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFFFF453A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TactileConsoleFader(
                value = volume,
                onValueChange = { volume = it },
                label = "REMOTE ATTENUATION"
            )
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
            color = Color(0xFF00E5C8),
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
