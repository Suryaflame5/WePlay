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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.OutputRouteCard
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DeviceDiscoveryScreen(
    devices: List<AudioOutputDevice>,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onToggleSelect: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0 = ALL, 1 = AURACAST, 2 = BLUETOOTH, 3 = WI-FI

    // Animated Sonar Radar Sweep
    val infiniteTransition = rememberInfiniteTransition(label = "sonar_radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F14))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 10.dp)
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
                    .background(Color(0xFF19202A))
                    .border(1.dp, Color(0xFF283442), RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFFF1F5F9)
                )
            }

            Text(
                text = "HARDWARE DISCOVERY & PROBING",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF777D86)
            )

            // Scan Trigger Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isScanning) Color(0xFFFF9500) else Color(0xFF00E5C8))
                    .clickable { if (isScanning) onStopScan() else onStartScan() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isScanning) "STOPPING" else "SCANNING",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                    color = Color(0xFF0A0D12)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Animated Sonar Radar Viewport (Acoustic scanning)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF111722))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.height / 2f * 0.85f

                // Concentric Range Rings
                listOf(0.35f, 0.65f, 0.95f).forEach { fraction ->
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = maxRadius * fraction,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Crosshairs
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.dp.toPx()
                )

                // Rotating Radar Sweep Beam
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    center.x + (cos(rad) * maxRadius).toFloat(),
                    center.y + (sin(rad) * maxRadius).toFloat()
                )
                drawLine(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF00E5C8), Color(0x0000E5C8)),
                        center = center,
                        radius = maxRadius
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Discovered Blip Targets
                val blipAngles = listOf(45.0, 130.0, 240.0, 315.0)
                blipAngles.take(devices.size.coerceAtLeast(1)).forEachIndexed { index, angle ->
                    val bRad = Math.toRadians(angle)
                    val dist = maxRadius * (0.45f + (index * 0.15f))
                    val bx = center.x + (cos(bRad) * dist).toFloat()
                    val by = center.y + (sin(bRad) * dist).toFloat()

                    drawCircle(
                        color = Color(0xFF00E5C8).copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = Offset(bx, by)
                    )
                    drawCircle(
                        color = Color(0xFF00E5C8),
                        radius = 3.5.dp.toPx(),
                        center = Offset(bx, by)
                    )
                }

                // Center Host Sensor
                drawCircle(color = Color(0xFF00E5C8), radius = 5.dp.toPx(), center = center)
            }

            // Status Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isScanning) Color(0xFF00E5C8) else Color(0xFFFF9500))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isScanning) "SONAR PROBE ACTIVE • BLUETOOTH LE & MDNS" else "STANDBY • ${devices.size} HARDWARE SINKS FOUND",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Filter Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF141922))
                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(6.dp))
                .padding(3.dp)
        ) {
            listOf("ALL", "AURACAST", "BT LE", "WI-FI").forEachIndexed { index, label ->
                val isSelected = selectedFilter == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) Color(0xFF00E5C8) else Color.Transparent)
                        .clickable { selectedFilter = index }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = WePlayTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = if (isSelected) Color(0xFF0A0D12) else Color(0xFF94A3B8)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Pixel Auracast Native Sheet Callout Card (Reference 5)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141A23))
                .border(1.dp, Color(0xFF242E3B), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A2330))
                        .border(1.dp, Color(0xFF00E5C8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📡",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nearby Auracast Broadcasts",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "Connect compatible LE Audio headphones directly or join existing WePlay broadcasts.",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF777D86)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Discovered Audio Sinks
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(devices, key = { it.id }) { device ->
                OutputRouteCard(
                    device = device,
                    onToggleSelect = onToggleSelect,
                    onVolumeChange = onVolumeChange
                )
            }
        }
    }
}
