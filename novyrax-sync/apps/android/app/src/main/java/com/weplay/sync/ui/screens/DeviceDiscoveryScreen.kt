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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.SonarRadarMesh
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 8: Master "Discover Devices" Screen.
 * Exact visual match to Screen 8 in the design reference:
 * - Top Bar: Back arrow "<" + "Discover Devices" + radar sync/refresh icon
 * - Hero: SonarRadarMesh with concentric rings, central phone silhouette, rotating sweep,
 *   3 electric blue node dots and connecting rays
 * - Device List with AirPods Pro (-72 dBm), JBL Flip 6 (-68 dBm), Sony WH-1000XM5 (-65 dBm)
 *   and dark pill "Connect" buttons
 */
@Composable
fun DeviceDiscoveryScreen(
    devices: List<AudioOutputDevice>,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onToggleSelect: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var connectedStates by remember {
        mutableStateOf(mapOf("airpods" to false, "jbl" to false, "sony" to false))
    }

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
                    imageVector = Icons.Default.ArrowBack,
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

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Discovered Device List (AirPods Pro, JBL Flip 6, Sony WH-1000XM5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DiscoveredDeviceRow(
                    name = "AirPods Pro",
                    signalDbm = "-72 dBm",
                    iconType = WePlayIconType.HEADPHONES,
                    isConnected = connectedStates["airpods"] == true,
                    onConnectClick = {
                        val curr = connectedStates["airpods"] ?: false
                        connectedStates = connectedStates + ("airpods" to !curr)
                    }
                )

                DiscoveredDeviceRow(
                    name = "JBL Flip 6",
                    signalDbm = "-68 dBm",
                    iconType = WePlayIconType.SPEAKER,
                    isConnected = connectedStates["jbl"] == true,
                    onConnectClick = {
                        val curr = connectedStates["jbl"] ?: false
                        connectedStates = connectedStates + ("jbl" to !curr)
                    }
                )

                DiscoveredDeviceRow(
                    name = "Sony WH-1000XM5",
                    signalDbm = "-65 dBm",
                    iconType = WePlayIconType.HEADPHONES,
                    isConnected = connectedStates["sony"] == true,
                    onConnectClick = {
                        val curr = connectedStates["sony"] ?: false
                        connectedStates = connectedStates + ("sony" to !curr)
                    }
                )
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
                        tint = WePlayTextPrimary,
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
                        color = WePlayTextSecondary
                    )
                }
            }

            // Dark Pill "Connect" Button
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
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isConnected) "Connected" else "Connect",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
