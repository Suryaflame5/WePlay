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
import androidx.compose.foundation.shape.CircleShape
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
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.components.FlowingCopperRibbon
import com.weplay.sync.ui.components.WePlayBottomNav
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayElectricCyan
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlayStatusGreen
import com.weplay.sync.ui.theme.WePlayStatusRed
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTealDark
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import kotlin.math.roundToInt

/**
 * Screen 5: Master Multi-Room Control Screen ("Multi-Room Control").
 * Exact visual fidelity to Screen 5 in the design reference:
 * - Top Bar: 4-pillar amber sound mark + "Multi-Room Control" + Settings gear
 * - Master Volume Card with large dB readout, waveform icon & glowing slider
 * - Dynamic Room Slider Cards: Shows real connected physical hardware outputs and
 *   active companion listener rooms
 * - Bottom backdrop: glowing copper ribbon with amber frequency spectrum bars
 */
@Composable
fun MultiOutputRoomScreen(
    activeOutputs: List<AudioOutputDevice>,
    listeners: List<ConnectedListener> = emptyList(),
    masterVolume: Float,
    isMasterMuted: Boolean,
    onMasterVolumeChange: (Float) -> Unit,
    onMasterMuteToggle: () -> Unit,
    onDeviceVolumeChange: (String, Float) -> Unit,
    onToggleDevice: (String) -> Unit,
    onAddDeviceClick: () -> Unit,
    onClose: () -> Unit,
    onNavigateToTab: (AppScreen) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                .padding(bottom = 90.dp)
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

            // 2. Master Volume Card with accurate dB readout
            val masterDb = ((masterVolume - 1.0f) * 24.0f)
            val masterDbFormatted = if (masterVolume <= 0.01f) "-∞ dB" else if (masterDb >= 0) "0.0 dB" else String.format("%.1f dB", masterDb)

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

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isMasterMuted) {
                                Text(
                                    text = "MUTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WePlayStatusRed,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(WePlayStatusRed.copy(alpha = 0.15f))
                                        .clickable { onMasterMuteToggle() }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            WePlayIcon(
                                type = WePlayIconType.EQUALIZER,
                                tint = if (isMasterMuted) WePlayStatusRed else WePlayOrangeGlow,
                                size = 18.dp,
                                modifier = Modifier.clickable { onMasterMuteToggle() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big Bold dB Readout
                    Text(
                        text = masterDbFormatted,
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

            // 2.5 Dual-Device Multi-Sink Audio Matrix Controller
            var isDualAudioEnabled by remember { mutableStateOf(true) }
            var latencyOffsetMs by remember { mutableFloatStateOf(12f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C141C))
                    .border(1.dp, if (isDualAudioEnabled) WePlayTealPrimary.copy(alpha = 0.5f) else WePlayBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isDualAudioEnabled) WePlayTealPrimary else WePlayTextMuted)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DUAL BLUETOOTH MATRIX",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WePlayTextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isDualAudioEnabled) "Dual-Sink Pipeline Active (2 Outputs)" else "Single Output Mode",
                                fontSize = 11.sp,
                                color = if (isDualAudioEnabled) WePlayTealGlow else WePlayTextSecondary
                            )
                        }

                        Switch(
                            checked = isDualAudioEnabled,
                            onCheckedChange = { isDualAudioEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = WePlayTealPrimary,
                                uncheckedThumbColor = WePlayTextMuted,
                                uncheckedTrackColor = Color(0xFF1E2838)
                            )
                        )
                    }

                    if (isDualAudioEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Acoustic Delay Calibration",
                                fontSize = 11.sp,
                                color = WePlayTextSecondary
                            )
                            Text(
                                text = "+${latencyOffsetMs.toInt()} ms sync",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTealGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        GlowHorizontalSlider(
                            value = (latencyOffsetMs / 50f).coerceIn(0f, 1f),
                            onValueChange = { latencyOffsetMs = it * 50f },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Dynamic Hardware Outputs & Companion Listener Rooms
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Physical Hardware Outputs
                if (activeOutputs.isNotEmpty()) {
                    activeOutputs.forEach { device ->
                        val devDb = if (device.volume <= 0.01f) "-∞ dB" else String.format("%.1f dB", (device.volume - 1.0f) * 24.0f)
                        val iconType = when (device.type) {
                            DeviceType.HEADPHONES -> WePlayIconType.HEADPHONES
                            else -> WePlayIconType.SPEAKER
                        }
                        val subtitle = if (device.isSelectedForPlayback) "Active Hardware Sink" else "Available Sink"

                        RoomControlCard(
                            title = device.name,
                            subtitle = subtitle,
                            iconType = iconType,
                            volume = device.volume,
                            onVolumeChange = { onDeviceVolumeChange(device.id, it) },
                            isEnabled = device.isSelectedForPlayback,
                            onToggleEnabled = { onToggleDevice(device.id) },
                            dbDisplay = devDb
                        )
                    }
                }

                // Connected Companion Listeners (Multi-phone broadcast)
                if (listeners.isNotEmpty()) {
                    listeners.forEach { listener ->
                        val listenerDb = String.format("%.1f dB", (0.85f - 1.0f) * 24.0f)
                        RoomControlCard(
                            title = listener.name,
                            subtitle = "Companion Phone (${listener.rttMs.toInt()}ms sync)",
                            iconType = WePlayIconType.HEADPHONES,
                            volume = 0.85f,
                            onVolumeChange = { },
                            isEnabled = !listener.isMuted,
                            onToggleEnabled = { },
                            dbDisplay = listenerDb
                        )
                    }
                } else {
                    // Informative WePlay Sync Room Card
                    RoomControlCard(
                        title = "WePlay Sync Network",
                        subtitle = "0 Companion Listeners (Wi-Fi Broadcast)",
                        iconType = WePlayIconType.ROUTE,
                        volume = masterVolume,
                        onVolumeChange = onMasterVolumeChange,
                        isEnabled = true,
                        onToggleEnabled = { },
                        dbDisplay = masterDbFormatted
                    )
                }

                // Add Device Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WePlaySurfaceElevated)
                        .border(1.dp, WePlayBorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { onAddDeviceClick() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Discover Devices & Add Listeners",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WePlayOrangeGlow
                    )
                }
            }
        }

        // Docked Bottom Navigation Bar
        WePlayBottomNav(
            currentTab = AppScreen.MULTI_OUTPUT_ROOM,
            onTabSelect = onNavigateToTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
                            tint = if (isEnabled) WePlayOrangePrimary else WePlayTextPrimary,
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Green/Cyan LED Meter Bar Underneath
            LedMeterBar(volume = volume, isEnabled = isEnabled)
        }
    }
}

/**
 * Precision glowing horizontal slider.
 */
@Composable
private fun GlowHorizontalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .height(26.dp)
            .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val fraction = (change.position.x / widthPx).coerceIn(0f, 1f)
                    onValueChange(fraction)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(WePlaySurfaceElevated)
        )

        // Filled Track with Radiant Amber Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = value.coerceIn(0.01f, 1f))
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(WePlayOrangeDark, WePlayOrangePrimary, WePlayOrangeGlow)
                    )
                )
        )

        // Thumb Dot
        val thumbOffset = (value.coerceIn(0f, 1f) * widthPx) - 8.dp.value
        Box(
            modifier = Modifier
                .padding(start = thumbOffset.coerceAtLeast(0f).dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, WePlayOrangePrimary, CircleShape)
        )
    }
}

/**
 * Segmented LED Volume Meter Bar (Green to Cyan gradient segments).
 */
@Composable
private fun LedMeterBar(
    volume: Float,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val segmentCount = 28
        val gap = 3.dp.toPx()
        val totalWidth = size.width
        val segmentWidth = (totalWidth - (gap * (segmentCount - 1))) / segmentCount
        val activeCount = if (isEnabled) (volume * segmentCount).roundToInt() else 0

        for (i in 0 until segmentCount) {
            val startX = i * (segmentWidth + gap)
            val isActive = i < activeCount

            val color = when {
                !isActive -> Color(0xFF1E2638)
                i < segmentCount * 0.7f -> WePlayStatusGreen
                else -> WePlayElectricCyan
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(startX, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
}

/**
 * Animated Amber Spectrum Bars at the bottom of the screen.
 */
@Composable
private fun BottomAmberSpectrum(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bottomSpectrum")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        val barCount = 42
        val gap = 4.dp.toPx()
        val barWidth = (size.width - (gap * (barCount - 1))) / barCount

        for (i in 0 until barCount) {
            val barPhase = phase + (i * 0.22f)
            val heightFraction = 0.25f + 0.65f * kotlin.math.abs(kotlin.math.sin(barPhase))
            val barHeight = size.height * heightFraction
            val startX = i * (barWidth + gap)
            val startY = size.height - barHeight

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(WePlayOrangeGlow.copy(alpha = 0.8f), WePlayOrangeDark.copy(alpha = 0.2f))
                ),
                topLeft = Offset(startX, startY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
}
