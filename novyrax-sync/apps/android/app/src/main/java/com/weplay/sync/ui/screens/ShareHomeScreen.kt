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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.components.WePlayBottomNav
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayStatusRed
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTealDark
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Master Dual Audio & Music Dashboard.
 * Crystal clear, beautiful, and completely intuitive for anyone to use.
 * - Shows connected Bluetooth headphones & speakers with individual tactile volume controls.
 * - One-tap Dual Audio listening mode.
 * - Integrated high-fidelity music player with instant track selection.
 * - Seamless QR sharing for friends with their own phones.
 */
@Composable
fun ShareHomeScreen(
    activeSession: AudioSharingSession,
    trackState: PlayerTrackState = PlayerTrackState(),
    isPlaying: Boolean = false,
    devices: List<AudioOutputDevice> = emptyList(),
    masterVolume: Float = 0.8f,
    onMasterVolumeChange: (Float) -> Unit = {},
    onDeviceVolumeChange: (String, Float) -> Unit = { _, _ -> },
    onToggleDevice: (String) -> Unit = {},
    onPlayPauseToggle: () -> Unit = {},
    onTrackSelect: (String, String, String) -> Unit = { _, _, _ -> },
    onOpenNowPlaying: () -> Unit = {},
    onStartSession: () -> Unit = {},
    onJoinSession: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenRooms: () -> Unit = {},
    onOpenDevices: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenLiveSession: () -> Unit = {},
    onOpenBluetoothSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isDualSoundActive by remember { mutableStateOf(true) }
    val bluetoothDevices = devices.filter { it.type == DeviceType.HEADPHONES || it.type == DeviceType.SPEAKER }
    val activeDeviceCount = bluetoothDevices.size.coerceAtLeast(if (devices.isNotEmpty()) 1 else 0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // 1. Top Bar: Brand + Connection Status Pill + Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WePlayMark(size = 28.dp, glowAlpha = 0.6f)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "WePlay",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WePlayTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Status pill: Shows if 2 devices or 1 device is connected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1822))
                        .border(1.dp, WePlayTealPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onOpenRooms() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (activeDeviceCount >= 2) WePlayTealGlow else WePlayTealPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                activeDeviceCount >= 2 -> "2 Devices • Playing to Both"
                                activeDeviceCount == 1 -> "1 Device Connected"
                                else -> "Phone Speaker"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WePlayTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Dual-Device Listening Center (Hero Feature!)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Connected Headphones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WePlayTextPrimary
                        )
                        Text(
                            text = "Control volume for each listener independently",
                            fontSize = 12.sp,
                            color = WePlayTextSecondary
                        )
                    }

                    // Dual Play Switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Dual Play",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDualSoundActive) WePlayTealGlow else WePlayTextMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isDualSoundActive,
                            onCheckedChange = { isDualSoundActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = WePlayTealPrimary,
                                uncheckedThumbColor = WePlayTextMuted,
                                uncheckedTrackColor = Color(0xFF1E2838)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Device Cards: Slot 1 & Slot 2
                val device1 = devices.getOrNull(0)
                val device2 = devices.getOrNull(1)

                // First Audio Device Card
                DeviceListeningCard(
                    deviceName = device1?.name ?: "Bluetooth Headphones 1",
                    deviceType = device1?.type ?: DeviceType.HEADPHONES,
                    volume = device1?.volume ?: masterVolume,
                    onVolumeChange = { v ->
                        device1?.let { onDeviceVolumeChange(it.id, v) } ?: onMasterVolumeChange(v)
                    },
                    isPlaying = isPlaying,
                    slotNumber = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Second Audio Device Card (or "+ Add 2nd Headphone" button)
                if (device2 != null) {
                    DeviceListeningCard(
                        deviceName = device2.name,
                        deviceType = device2.type,
                        volume = device2.volume,
                        onVolumeChange = { v -> onDeviceVolumeChange(device2.id, v) },
                        isPlaying = isPlaying && isDualSoundActive,
                        slotNumber = 2
                    )
                } else {
                    // Inviting card to pair a second headphone or speaker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F1522))
                            .border(1.dp, WePlayBorderSubtle, RoundedCornerShape(16.dp))
                            .clickable { onOpenBluetoothSettings() }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF162030)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = WePlayTealGlow,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "+ Connect 2nd Headphone / Speaker",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = WePlayTextPrimary
                                    )
                                    Text(
                                        text = "Share audio with a friend wirelessly",
                                        fontSize = 11.sp,
                                        color = WePlayTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = "Pair",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTealPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WePlayTealPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Integrated Music Player Card (Play with 1 tap!)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Music Player",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF131D2E), Color(0xFF0F1624))
                            )
                        )
                        .border(1.dp, WePlayBorder, RoundedCornerShape(20.dp))
                        .clickable { onOpenNowPlaying() }
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini Vinyl Album Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0A0F18))
                                    .border(1.dp, WePlayBorder, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val cx = w * 0.5f
                                    val cy = h * 0.5f
                                    drawCircle(Color(0xFF141E2C), radius = cx * 0.85f, center = Offset(cx, cy))
                                    drawCircle(WePlayTealPrimary, radius = cx * 0.35f, center = Offset(cx, cy))
                                    drawCircle(Color.White, radius = cx * 0.12f, center = Offset(cx, cy))
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = trackState.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WePlayTextPrimary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${trackState.artist} • Lossless Audio",
                                    fontSize = 12.sp,
                                    color = WePlayTealGlow,
                                    maxLines = 1
                                )
                            }

                            // Big Satisfying Play / Pause Button
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(WePlayTealGlow, WePlayTealPrimary, WePlayTealDark)
                                        )
                                    )
                                    .clickable { onPlayPauseToggle() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlaying) {
                                    // Pause icon
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.width(3.dp).height(14.dp).background(Color.White, RoundedCornerShape(1.dp)))
                                        Box(modifier = Modifier.width(3.dp).height(14.dp).background(Color.White, RoundedCornerShape(1.dp)))
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Line
                        val currentMs = trackState.currentPositionMs
                        val durMs = trackState.durationMs.coerceAtLeast(1000L)
                        val frac = (currentMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        ) {
                            val w = size.width
                            val h = size.height
                            drawRoundRect(Color(0xFF1E2838), size = Size(w, h), cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()))
                            drawRoundRect(
                                brush = Brush.horizontalGradient(listOf(WePlayTealDark, WePlayTealPrimary)),
                                size = Size(w * frac, h),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Quick Song Selection (Instant Play)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Songs & Soundscapes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WePlayTextPrimary
                    )
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        color = WePlayTealPrimary,
                        modifier = Modifier.clickable { onOpenLibrary() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val sampleTracks = listOf(
                    Triple("Midnight Horizons", "Ambient Atlas", "Atlas"),
                    Triple("Velvet Night", "Solar Sound", "Chill"),
                    Triple("Deep Focus Pulse", "Nova Labs", "Focus"),
                    Triple("Ocean Waves 3D", "Nature Binaural", "Sleep")
                )

                sampleTracks.forEach { (title, artist, album) ->
                    val isCurrent = trackState.title == title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrent) Color(0xFF142030) else Color(0xFF0F1520))
                            .border(1.dp, if (isCurrent) WePlayTealPrimary.copy(alpha = 0.5f) else WePlayBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable {
                                onTrackSelect(title, artist, album)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A2638)),
                                contentAlignment = Alignment.Center
                            ) {
                                WePlayIcon(
                                    type = if (isCurrent && isPlaying) WePlayIconType.EQUALIZER else WePlayIconType.HEADPHONES,
                                    tint = if (isCurrent) WePlayTealGlow else WePlayTextSecondary,
                                    size = 18.dp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCurrent) WePlayTealGlow else WePlayTextPrimary
                                )
                                Text(
                                    text = artist,
                                    fontSize = 11.sp,
                                    color = WePlayTextSecondary
                                )
                            }
                        }

                        Text(
                            text = if (isCurrent && isPlaying) "Playing" else "Play",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) WePlayTealGlow else WePlayTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Share with Friends (Crystal Simple QR Sharing)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Listen With Friends (2+ Phones)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Friend has their own phone? Let them listen with their own headphones!",
                    fontSize = 12.sp,
                    color = WePlayTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: Share (Show QR)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF147A6E), WePlayTealPrimary)
                                )
                            )
                            .clickable { onStartSession() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Share My Audio (QR)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Button 2: Join (Scan Friend's QR)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF141C2B))
                            .border(1.dp, WePlayBorder, RoundedCornerShape(24.dp))
                            .clickable { onJoinSession() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Scan Friend's QR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WePlayTextPrimary
                        )
                    }
                }
            }
        }

        // Docked Bottom Navigation Bar
        WePlayBottomNav(
            currentTab = AppScreen.SHARE_HOME,
            onTabSelect = { target ->
                when (target) {
                    AppScreen.LIBRARY -> onOpenLibrary()
                    AppScreen.MULTI_OUTPUT_ROOM -> onOpenRooms()
                    AppScreen.SETTINGS -> onOpenSettings()
                    else -> Unit
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Individual tactile card for connected listening devices (AirPods, Studio Monitors, etc.)
 */
@Composable
private fun DeviceListeningCard(
    deviceName: String,
    deviceType: DeviceType,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isPlaying: Boolean,
    slotNumber: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1522))
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
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF182232)),
                        contentAlignment = Alignment.Center
                    ) {
                        WePlayIcon(
                            type = if (deviceType == DeviceType.HEADPHONES) WePlayIconType.HEADPHONES else WePlayIconType.SPEAKER,
                            tint = WePlayTealGlow,
                            size = 20.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Output $slotNumber: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTealPrimary
                            )
                            Text(
                                text = deviceName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTextPrimary,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = if (isPlaying) "Playing in sync" else "Connected & Ready",
                            fontSize = 11.sp,
                            color = if (isPlaying) WePlayTealGlow else WePlayTextSecondary
                        )
                    }
                }

                // Volume % Readout
                Text(
                    text = "${(volume * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTealGlow
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Smooth tactile volume slider
            GlowHorizontalSlider(
                value = volume,
                onValueChange = onVolumeChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
