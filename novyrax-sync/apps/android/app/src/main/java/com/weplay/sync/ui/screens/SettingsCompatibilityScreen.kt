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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.theme.WePlayTheme

@Composable
fun SettingsCompatibilityScreen(
    onOpenDiagnostics: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F14))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
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
                text = "HAL SPEC & HARDWARE CONSTRAINTS",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF00E5C8)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Compatibility & Policy Matrix",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 24.sp),
            color = Color(0xFFF1F5F9)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Honest, verifiable operating boundaries of Android AudioPlaybackCapture, DRM, and Auracast.",
            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = Color(0xFF777D86)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. System Permissions Status
        Text(
            text = "SYSTEM PERMISSIONS STATUS",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PermissionStatusCard(
            title = "Audio Playback Capture",
            subtitle = "Android MediaProjection token required for each cross-app capture session",
            isGranted = true
        )
        Spacer(modifier = Modifier.height(6.dp))
        PermissionStatusCard(
            title = "Nearby Devices & Bluetooth",
            subtitle = "Scan and advertise LE Audio Auracast and Bluetooth routes",
            isGranted = true
        )
        Spacer(modifier = Modifier.height(6.dp))
        PermissionStatusCard(
            title = "Background Battery Optimization Exemption",
            subtitle = "Allows uninterrupted audio distribution while switching away from WePlay",
            isGranted = true
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 3. Source Application Support Matrix (Rigorous & Honest)
        Text(
            text = "SOURCE APPLICATION COMPATIBILITY MATRIX",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppCompatibilityRow(
            appName = "YouTube (Free & Premium)",
            status = "CONDITIONAL",
            statusColor = Color(0xFFFF9500),
            description = "Standard user videos capture cleanly. Paid movies, rented titles, and Widevine DRM streams are silenced by the OS."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "Spotify & Podcasts",
            status = "CONDITIONAL",
            statusColor = Color(0xFFFF9500),
            description = "Standard media playback captures when target app doesn't set CAPTURE_POLICY_NONE. May vary across OEM Android skins."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "Chrome & Web Browsers",
            status = "CONDITIONAL",
            statusColor = Color(0xFFFF9500),
            description = "HTML5 audio/video tags allow capture by default. Web DRM video players in browser will be muted by Android HAL."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "Mobile Games & Local Players",
            status = "CONDITIONAL",
            statusColor = Color(0xFFFF9500),
            description = "Eligible if audio attributes use USAGE_MEDIA or USAGE_GAME. Custom low-latency OpenSL ES direct HAL streams may bypass capture."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "DRM Services (Netflix, Prime, Disney+)",
            status = "UNSUPPORTED (BY DESIGN)",
            statusColor = Color(0xFFFF3B30),
            description = "Protected by Widevine L1 and FLAG_SECURE. Android HAL strictly zeros audio capture buffers for copyright protection."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "VoIP & Phone Calls (WhatsApp, Zoom, Cellular)",
            status = "UNSUPPORTED (BY DESIGN)",
            statusColor = Color(0xFFFF3B30),
            description = "USAGE_VOICE_COMMUNICATION is barred from AudioPlaybackCapture by Android privacy rules to prevent call recording."
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 4. Auracast Technical Clarification Card
        Text(
            text = "BLUETOOTH AURACAST ARCHITECTURAL BOUNDARY",
            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
            color = Color(0xFF777D86)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141922))
                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hardware Probe vs System API",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "OEM PRIVILEGED",
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                        color = Color(0xFFFF9500)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "isLeAudioBroadcastSourceSupported() verifies physical radio capability. However, creating an Auracast broadcast requires @SystemApi MANAGE_BLUETOOTH_LE_BROADCAST_SOURCE, restricted to platform-signed system apps. WePlay directs supported phones to native system Bluetooth Audio Sharing and utilizes low-latency Wi-Fi transport (WADP v1.0) for universal multi-phone sync.",
                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 5. Diagnostics Quick Access
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141922))
                .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(8.dp))
                .clickable { onOpenDiagnostics() }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Technical Diagnostics & Telemetry",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Text(
                        text = "Inspect true jitter, sequence gaps, packet loss, and round-trip times",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF777D86)
                    )
                }
                Text(
                    text = "VIEW ›",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFF00E5C8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PermissionStatusCard(
    title: String,
    subtitle: String,
    isGranted: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF141922))
            .border(1.dp, Color(0xFF222B38), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) Color(0xFF00E5C8).copy(alpha = 0.15f) else Color(0xFFFF3B30).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGranted) "✓" else "✕",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (isGranted) Color(0xFF00E5C8) else Color(0xFFFF3B30)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Color(0xFF777D86)
                )
            }
        }
    }
}

@Composable
fun AppCompatibilityRow(
    appName: String,
    status: String,
    statusColor: Color,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF141922))
            .border(1.dp, Color(0xFF222B38), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appName,
                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = Color(0xFFF1F5F9)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = status,
                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                    color = statusColor
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 15.sp),
            color = Color(0xFF777D86)
        )
    }
}
