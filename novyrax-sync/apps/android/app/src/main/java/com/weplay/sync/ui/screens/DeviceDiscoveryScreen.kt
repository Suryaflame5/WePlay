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
import com.weplay.sync.ui.components.InstrumentChassisCard
import com.weplay.sync.ui.components.OutputRouteCard
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
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = AtelierTextPrimary
                )
            }

            Text(
                text = "HARDWARE DISCOVERY & SENSOR PROBE",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
                color = ElectricCobalt
            )

            // Scan Trigger Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isScanning) Color(0xFFFF9500) else SignalVermilion)
                    .clickable { if (isScanning) onStopScan() else onStartScan() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isScanning) "STOPPING" else "SCANNING",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Animated Sonar Radar Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AtelierTray)
                .border(1.dp, AtelierChamfer, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.height / 2f * 0.85f

                // Concentric Range Rings
                listOf(0.35f, 0.65f, 0.95f).forEach { fraction ->
                    drawCircle(
                        color = AtelierChamfer,
                        radius = maxRadius * fraction,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Crosshairs
                drawLine(
                    color = AtelierChamfer,
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = AtelierChamfer,
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
                        listOf(ElectricCobalt, Color(0x001A62FF)),
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
                        color = SignalVermilion.copy(alpha = 0.35f),
                        radius = 8.dp.toPx(),
                        center = Offset(bx, by)
                    )
                    drawCircle(
                        color = SignalVermilion,
                        radius = 3.5.dp.toPx(),
                        center = Offset(bx, by)
                    )
                }

                // Center Host Sensor
                drawCircle(color = SignalVermilion, radius = 5.dp.toPx(), center = center)
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
                        .background(if (isScanning) SignalVermilion else AtelierTextSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isScanning) "SONAR PROBE ACTIVE • BLUETOOTH LE & MDNS" else "STANDBY • ${devices.size} HARDWARE SINKS FOUND",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp),
                    color = AtelierTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Filter Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(AtelierTray)
                .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
                .padding(3.dp)
        ) {
            listOf("ALL", "AURACAST", "BT LE", "WI-FI").forEachIndexed { index, label ->
                val isSelected = selectedFilter == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) SignalVermilion else Color.Transparent)
                        .clickable { selectedFilter = index }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = WePlayTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = if (isSelected) Color.White else AtelierTextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Pixel Auracast Native Sheet Callout Card
        InstrumentChassisCard(
            label = "NEARBY AURACAST STREAMS",
            statusIndicator = ElectricCobalt
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AtelierTray)
                        .border(1.dp, ElectricCobalt, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📡", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auracast Broadcast Discovery",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        color = AtelierTextPrimary
                    )
                    Text(
                        text = "Connect compatible LE Audio headphones directly or bind into existing WePlay streams.",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = AtelierTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Discovered Audio Sinks
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
