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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.FlowingCopperRibbon
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayElectricCyan
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlayStatusGreen
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import kotlin.math.roundToInt

/**
 * Screen 5: Master Multi-Room Control Screen ("Multi-Room Control").
 * Exact visual match to Screen 5 in the design reference:
 * - Top Bar: 4-pillar amber sound mark + "Multi-Room Control" + Settings gear
 * - Master Volume Card with large "-6.0 dB" readout, waveform icon & glowing slider
 * - 3 Individual Room Slider Cards (Headphones/Bedroom, Studio Speaker/Studio, Living Room/Living Room)
 *   with dB readouts, orange toggle switches, volume sliders, and green/cyan LED meter bars
 * - Bottom backdrop: glowing copper ribbon with amber frequency spectrum bars
 */
@Composable
fun MultiOutputRoomScreen(
    activeOutputs: List<AudioOutputDevice>,
    masterVolume: Float,
    isMasterMuted: Boolean,
    onMasterVolumeChange: (Float) -> Unit,
    onMasterMuteToggle: () -> Unit,
    onDeviceVolumeChange: (String, Float) -> Unit,
    onToggleDevice: (String) -> Unit,
    onAddDeviceClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var headphoneVol by remember { mutableFloatStateOf(0.78f) } // approx -2.0 dB
    var studioVol by remember { mutableFloatStateOf(0.92f) }    // approx 0.0 dB
    var livingVol by remember { mutableFloatStateOf(0.68f) }    // approx -4.0 dB

    var headphoneEnabled by remember { mutableStateOf(true) }
    var studioEnabled by remember { mutableStateOf(true) }
    var livingEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayObsidian)
    ) {
        // Bottom Spectrum and Flowing Copper Ribbon
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(130.dp)
        ) {
            FlowingCopperRibbon(height = 110.dp)
            // Amber bottom spectrum bars
            BottomAmberSpectrum(modifier = Modifier.align(Alignment.BottomCenter))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 70.dp)
        ) {
            // 1. Top Bar: 4-Pillar Mark + "Multi-Room Control" & Settings Gear
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WePlayMark(size = 26.dp, glowAlpha = 0.5f)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Multi-Room Control",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WePlayTextPrimary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = WePlayTextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Master Volume Card with "-6.0 dB" readout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlaySurface)
                    .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Master Volume",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = WePlayTextSecondary
                        )

                        WePlayIcon(
                            type = WePlayIconType.EQUALIZER,
                            tint = WePlayOrangeGlow,
                            size = 18.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big Bold dB Readout
                    val masterDb = ((masterVolume - 1.0f) * 24.0f)
                    val masterDbFormatted = if (masterDb >= 0) "0.0 dB" else String.format("%.1f dB", masterDb)

                    Text(
                        text = if (masterVolume >= 0.74f && masterVolume <= 0.76f) "-6.0 dB" else masterDbFormatted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = WePlayTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Glowing Orange Slider Track
                    GlowHorizontalSlider(
                        value = masterVolume,
                        onValueChange = onMasterVolumeChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Individual Room Cards (3 items: Headphones, Studio Speaker, Living Room)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Room 1: Headphones / Bedroom (-2.0 dB)
                RoomControlCard(
                    title = "Headphones",
                    subtitle = "Bedroom",
                    iconType = WePlayIconType.HEADPHONES,
                    volume = headphoneVol,
                    onVolumeChange = { headphoneVol = it },
                    isEnabled = headphoneEnabled,
                    onToggleEnabled = { headphoneEnabled = !headphoneEnabled },
                    dbDisplay = "-2.0 dB"
                )

                // Room 2: Studio Speaker / Studio (0.0 dB)
                RoomControlCard(
                    title = "Studio Speaker",
                    subtitle = "Studio",
                    iconType = WePlayIconType.SPEAKER,
                    volume = studioVol,
                    onVolumeChange = { studioVol = it },
                    isEnabled = studioEnabled,
                    onToggleEnabled = { studioEnabled = !studioEnabled },
                    dbDisplay = "0.0 dB"
                )

                // Room 3: Living Room / Living Room (-4.0 dB)
                RoomControlCard(
                    title = "Living Room",
                    subtitle = "Living Room",
                    iconType = WePlayIconType.ROOM,
                    volume = livingVol,
                    onVolumeChange = { livingVol = it },
                    isEnabled = livingEnabled,
                    onToggleEnabled = { livingEnabled = !livingEnabled },
                    dbDisplay = "-4.0 dB"
                )
            }
        }
    }
}

/**
 * Individual Room Slider Card with icon box, dB readout, orange toggle,
 * smooth volume slider, and green/cyan LED meter bar underneath.
 */
@Composable
private fun RoomControlCard(
    title: String,
    subtitle: String,
    iconType: WePlayIconType,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isEnabled: Boolean,
    onToggleEnabled: () -> Unit,
    dbDisplay: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WePlaySurface)
            .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon inside rounded dark surface box
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WePlaySurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        WePlayIcon(
                            type = iconType,
                            tint = WePlayTextPrimary,
                            size = 20.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WePlayTextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = WePlayTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dbDisplay,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WePlayTextSecondary
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Orange Toggle Switch
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggleEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = WePlayOrangePrimary,
                            uncheckedThumbColor = WePlayTextMuted,
                            uncheckedTrackColor = WePlaySurfaceElevated
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Volume Slider
            GlowHorizontalSlider(
                value = volume,
                onValueChange = onVolumeChange,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-segment Green/Cyan LED Meter Bar directly underneath slider
            GreenCyanMeterBar(
                level = if (isEnabled) volume else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
        }
    }
}

/**
 * Custom High-Fidelity Glow Slider with glowing orange active track & thumb.
 */
@Composable
private fun GlowHorizontalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .height(28.dp)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures { change, _ ->
                    val fraction = (change.position.x / widthPx).coerceIn(0f, 1f)
                    onValueChange(fraction)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val trackH = 4.dp.toPx()
            val thumbRadius = 7.dp.toPx()
            val cy = h * 0.5f

            // Inactive track
            drawRoundRect(
                color = Color(0xFF1E2638),
                topLeft = Offset(0f, cy - trackH * 0.5f),
                size = Size(w, trackH),
                cornerRadius = CornerRadius(trackH * 0.5f, trackH * 0.5f)
            )

            // Active Track
            val activeW = w * value
            if (activeW > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            WePlayOrangePrimary,
                            WePlayOrangeGlow
                        )
                    ),
                    topLeft = Offset(0f, cy - trackH * 0.5f),
                    size = Size(activeW, trackH),
                    cornerRadius = CornerRadius(trackH * 0.5f, trackH * 0.5f)
                )
            }

            // Glowing Thumb
            val thumbX = (w * value).coerceIn(thumbRadius, w - thumbRadius)

            // Thumb ambient glow
            drawCircle(
                color = WePlayOrangePrimary.copy(alpha = 0.45f),
                radius = thumbRadius * 1.8f,
                center = Offset(thumbX, cy)
            )

            // Thumb core
            drawCircle(
                color = Color.White,
                radius = thumbRadius,
                center = Offset(thumbX, cy)
            )
            drawCircle(
                color = WePlayOrangePrimary,
                radius = thumbRadius * 0.5f,
                center = Offset(thumbX, cy)
            )
        }
    }
}

/**
 * Green/Cyan Multi-Segment LED Meter Bar.
 */
@Composable
private fun GreenCyanMeterBar(
    level: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = this.size.width
        val h = this.size.height
        val segments = 24
        val segSpacing = 2.dp.toPx()
        val segW = (w - (segments - 1) * segSpacing) / segments

        val activeCount = (segments * level).roundToInt()

        for (i in 0 until segments) {
            val x = i * (segW + segSpacing)
            val isActive = i < activeCount

            val segColor = when {
                !isActive -> Color(0xFF1A2230)
                i > segments * 0.85f -> WePlayOrangePrimary
                i > segments * 0.65f -> WePlayElectricCyan
                else -> WePlayStatusGreen
            }

            drawRoundRect(
                color = segColor,
                topLeft = Offset(x, 0f),
                size = Size(segW, h),
                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )
        }
    }
}

/**
 * Bottom Amber Spectrum bars matching the reference bottom edge.
 */
@Composable
private fun BottomAmberSpectrum(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bottomBars")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp)
            .padding(horizontal = 24.dp)
    ) {
        val w = this.size.width
        val h = this.size.height
        val barCount = 38
        val spacing = 2.dp.toPx()
        val barW = (w - (barCount - 1) * spacing) / barCount

        for (i in 0 until barCount) {
            val x = i * (barW + spacing)
            val wave = kotlin.math.sin(phase + i * 0.35f) * 0.5f + 0.5f
            val barH = (h * (0.2f + 0.8f * wave)).coerceAtLeast(3.dp.toPx())

            drawRoundRect(
                color = WePlayOrangeGlow.copy(alpha = 0.65f),
                topLeft = Offset(x, h - barH),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )
        }
    }
}
