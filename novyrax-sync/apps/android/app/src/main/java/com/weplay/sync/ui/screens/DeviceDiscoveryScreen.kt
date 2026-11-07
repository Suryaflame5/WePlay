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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.ui.components.SonarRadarMesh
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 8: Master "Discover Devices" Screen.
 * Exact visual fidelity to Screen 8 in the design reference:
 * - Top Bar: Back arrow "<" + "Discover Devices" + radar sync/refresh icon
 * - Hero: SonarRadarMesh with concentric rings, central phone silhouette, rotating radar sweep
 * - Real hardware device discovery (reads actual bonded Bluetooth devices & system outputs)
 * - Honest platform status: Displays Bluetooth Off banner or Permission banner when needed
 * - Real Connect / Route action buttons
 */
@Composable
fun DeviceDiscoveryScreen(
    devices: List<AudioOutputDevice>,
    isBluetoothEnabled: Boolean = true,
    hasBluetoothPermission: Boolean = true,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onToggleSelect: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    onOpenBluetoothSettings: () -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: Back Arrow, Title, Refresh Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WePlayTextPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = "Discover Devices",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Scan",
                    tint = WePlayTextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onStartScan() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Central Sonar Radar Mesh Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                SonarRadarMesh(size = 260.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Hardware State Banners & Real Device List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!isBluetoothEnabled) {
                    // Bluetooth is Disabled Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(WePlaySurface)
                            .border(1.dp, WePlayOrangeDark, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                WePlayIcon(
                                    type = WePlayIconType.ROUTE,
                                    tint = WePlayOrangeGlow,
                                    size = 20.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Bluetooth is Turned Off",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WePlayTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Turn on Bluetooth in system settings to discover and route audio to your wireless headphones or speakers.",
                                fontSize = 12.sp,
                                color = WePlayTextSecondary,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WePlayOrangePrimary)
                                    .clickable { onOpenBluetoothSettings() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Open Bluetooth Settings",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else if (!hasBluetoothPermission) {
                    // Permission Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(WePlaySurface)
                            .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Nearby Devices Permission Needed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Android requires Nearby Devices permission to scan and connect paired Bluetooth audio hardware.",
                                fontSize = 12.sp,
                                color = WePlayTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WePlayOrangePrimary)
                                    .clickable { onRequestPermission() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Grant Permission",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Discovered / Paired Devices
                if (devices.isNotEmpty()) {
                    devices.forEach { device ->
                        val iconType = when (device.type) {
                            DeviceType.HEADPHONES -> WePlayIconType.HEADPHONES
                            DeviceType.SYSTEM_BUILTIN -> WePlayIconType.SPEAKER
                            else -> WePlayIconType.SPEAKER
                        }

                        val subtitle = if (device.isSelectedForPlayback) {
                            "Active Audio Output"
                        } else {
                            device.hardwareAddress ?: "Available Output"
                        }

                        DiscoveredDeviceRow(
                            name = device.name,
                            signalDbm = subtitle,
                            iconType = iconType,
                            isConnected = device.isSelectedForPlayback,
                            onConnectClick = {
                                onToggleSelect(device.id)
                            }
                        )
                    }
                } else if (isBluetoothEnabled) {
                    // Empty state when Bluetooth is on but no devices found
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(WePlaySurface)
                            .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WePlayIcon(
                                type = WePlayIconType.HEADPHONES,
                                tint = WePlayTextMuted,
                                size = 32.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Paired Audio Devices Found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WePlayTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Put your Bluetooth headphones or speaker into pairing mode, then pair them in your device settings.",
                                fontSize = 12.sp,
                                color = WePlayTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WePlaySurfaceElevated)
                                    .border(1.dp, WePlayBorderSubtle, RoundedCornerShape(12.dp))
                                    .clickable { onOpenBluetoothSettings() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Pair New Device",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = WePlayTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Honest Technical Architecture Clarification Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF10141D))
                        .border(1.dp, WePlayBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Multi-Device Audio Notice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayOrangeGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Standard Android hardware routes media to one active Bluetooth accessory per phone. To listen together through multiple pairs of headphones, use WePlay's Host Broadcast to stream synchronized lossless audio across multiple companion phones over Wi-Fi!",
                            fontSize = 11.sp,
                            color = WePlayTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoveredDeviceRow(
    name: String,
    signalDbm: String,
    iconType: WePlayIconType,
    isConnected: Boolean,
    onConnectClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WePlaySurface)
            .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Device Icon in Circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(WePlaySurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    WePlayIcon(
                        type = iconType,
                        tint = if (isConnected) WePlayOrangePrimary else WePlayTextPrimary,
                        size = 20.dp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WePlayTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = signalDbm,
                        fontSize = 11.sp,
                        color = if (isConnected) WePlayOrangeGlow else WePlayTextSecondary
                    )
                }
            }

            // Dark Pill "Connect" / "Active Route" Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isConnected) WePlayOrangePrimary else Color(0xFF161D2B)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isConnected) WePlayOrangePrimary else WePlayBorderSubtle,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onConnectClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isConnected) "Active" else "Select Route",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
