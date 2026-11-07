package com.weplay.sync.ui.components

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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.DeviceType
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
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Precision Milled Instrument Chassis Container
 * Features machine-etched corner registration marks and milled borders.
 */
@Composable
fun InstrumentChassisCard(
    modifier: Modifier = Modifier,
    label: String? = null,
    statusIndicator: Color? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AtelierGunmetal)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
            .padding(14.dp)
    ) {
        if (label != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (statusIndicator != null) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusIndicator)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = label.uppercase(),
                        style = WePlayTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 1.6.sp
                        ),
                        color = AtelierTextSecondary
                    )
                }

                // Laser-etched Chassis Spec ID
                Text(
                    text = "SPEC. 2026-HAL",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                    color = AtelierChamfer
                )
            }
        }
        content()
    }
}

/**
 * Kinetic Acoustic Bus Rail
 * Visually routes master audio pipeline to active listener sinks.
 * Strictly reactive: pulses and animates ONLY when audio transmission is active.
 */
@Composable
fun AcousticBusRail(
    isTransmitting: Boolean,
    listenerCount: Int,
    modifier: Modifier = Modifier,
    transportFormat: String = "LPCM 48kHz / 16-bit STEREO"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bus_rail")
    val pulseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_flow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(AtelierTray)
            .border(1.dp, AtelierChamfer, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Background Bus Rail Conduit Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            val railY = h * 0.65f

            // Copper ground bus bar
            drawLine(
                color = AtelierChamfer,
                start = Offset(0f, railY),
                end = Offset(w, railY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Dynamic Transmission Energy Packet Pulses (Only when active)
            if (isTransmitting) {
                val packetSpacing = w / 4f
                for (p in 0..4) {
                    val px = ((pulseOffset * packetSpacing) + (p * packetSpacing)) % w
                    drawCircle(
                        color = SignalVermilion,
                        radius = 3.5.dp.toPx(),
                        center = Offset(px, railY)
                    )
                    drawCircle(
                        color = SignalVermilion.copy(alpha = 0.3f),
                        radius = 7.dp.toPx(),
                        center = Offset(px, railY)
                    )
                }
            }
        }

        // Telemetry Overlay
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isTransmitting) SignalVermilion else AtelierTextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTransmitting) "TRANSMISSION BUS ACTIVE" else "STANDBY // BUS IDLE",
                        style = WePlayTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (isTransmitting) SignalVermilion else AtelierTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transportFormat,
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = AtelierTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(AtelierGunmetal)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$listenerCount SINKS BOUND",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = if (listenerCount > 0) ElectricCobalt else AtelierTextSecondary
                )
            }
        }
    }
}

/**
 * Horological Planetary Rotary Control Dial
 * Features milled outer bezel, circular decibel graduation ticks, and open aperture readout.
 */
@Composable
fun PlanetaryRotaryDial(
    valuePercent: Int, // 0 to 100
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "MASTER BUS GAIN"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.4.sp),
            color = AtelierTextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(AtelierObsidian)
                .border(1.dp, AtelierChamfer, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val angleRad = kotlin.math.atan2(offset.y - center.y, offset.x - center.x)
                            var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat() + 90f
                            if (angleDeg < 0) angleDeg += 360f
                            val newPct = ((angleDeg / 360f) * 100f).roundToInt().coerceIn(0, 100)
                            onValueChange(newPct)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        val delta = (dragAmount * 0.45f).roundToInt()
                        val newPct = (valuePercent + delta).coerceIn(0, 100)
                        onValueChange(newPct)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = size.width / 2f - 12.dp.toPx()
                val innerRadius = outerRadius - 20.dp.toPx()

                // Recessed track ring
                drawCircle(
                    color = AtelierTray,
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Active Arc
                val sweepAngle = (valuePercent / 100f) * 360f
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(SignalVermilion, ElectricCobalt, SignalVermilion)
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // 40 Precision Chamfer Graduation Ticks
                val tickCount = 40
                for (i in 0 until tickCount) {
                    val angle = (i.toFloat() / tickCount) * 2 * Math.PI.toFloat() - (Math.PI / 2).toFloat()
                    val isMajor = i % 4 == 0
                    val tickLen = if (isMajor) 6.dp.toPx() else 3.5.dp.toPx()
                    val startR = outerRadius + 4.dp.toPx()
                    val endR = startR + tickLen

                    val startX = center.x + cos(angle) * startR
                    val startY = center.y + sin(angle) * startR
                    val endX = center.x + cos(angle) * endR
                    val endY = center.y + sin(angle) * endR

                    val isActive = (i.toFloat() / tickCount) <= (valuePercent / 100f)
                    drawLine(
                        color = if (isActive) SignalVermilion else AtelierChamfer,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                    )
                }

                // Inner aperture lens disc
                drawCircle(
                    color = AtelierGunmetal,
                    radius = innerRadius,
                    center = center
                )
                drawCircle(
                    color = AtelierChamfer,
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Central Aperture Readout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val dbEquivalent = if (valuePercent == 0) "-INF" else "%+.1f dB".format((valuePercent - 100) * 0.4f)
                Text(
                    text = "$valuePercent%",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 36.sp),
                    color = AtelierTextPrimary
                )
                Text(
                    text = dbEquivalent,
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                    color = SignalVermilion
                )
            }
        }
    }
}

/**
 * Modular Sink Pod (Eurorack-style destination chassis)
 * Represents an individual connected listener with live telemetry and attenuation.
 */
@Composable
fun ModularSinkPod(
    listener: ConnectedListener,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            // Hardware Vector Glyph
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                HardwareVectorGlyph(type = listener.deviceType, tint = ElectricCobalt)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listener.name,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = AtelierTextPrimary
                )
                Text(
                    text = "${listener.transport.displayName} • +${listener.clockOffsetMs}ms drift • ${listener.rttMs}ms RTT",
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                    color = AtelierTextSecondary
                )
            }

            // Quick drop latch
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(AtelierTray)
                    .border(1.dp, AtelierChamfer, RoundedCornerShape(3.dp))
                    .clickable { onDisconnect() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "DISCONNECT",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = Color(0xFFFF453A)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TactileConsoleFader(
            value = volume,
            onValueChange = onVolumeChange,
            label = "ATTENUATION"
        )
    }
}

/**
 * High-Contrast Vector QR Matrix with Centered WePlay Emblem
 */
@Composable
fun WePlayStreamQRCode(sessionCode: String) {
    RealQRCode(content = sessionCode.ifEmpty { "WP-4829" })
}
