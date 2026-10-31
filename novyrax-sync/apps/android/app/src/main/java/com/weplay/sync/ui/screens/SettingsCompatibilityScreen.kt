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
import com.weplay.sync.ui.components.WePlayMark
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
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = "‹ BACK",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }

            Text(
                text = "SETTINGS & COMPATIBILITY",
                style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Compatibility & Permissions",
            style = WePlayTheme.typography.displayLarge.copy(fontSize = 26.sp),
            color = WePlayTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "How WePlay shares audio without modifying other apps or requiring root privileges.",
            style = WePlayTheme.typography.bodyMedium,
            color = WePlayTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // System Permissions Status
        Text(
            text = "SYSTEM PERMISSIONS STATUS",
            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            color = WePlayTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        PermissionStatusCard(
            title = "Audio Playback Capture",
            subtitle = "Android MediaProjection user authorization for third-party apps",
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
            subtitle = "Allows uninterrupted audio distribution while watching YouTube",
            isGranted = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Source Application Support Matrix (Honest & Transparent)
        Text(
            text = "SOURCE APPLICATION COMPATIBILITY",
            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            color = WePlayTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppCompatibilityRow(
            appName = "YouTube, Spotify & Podcasts",
            status = "FULLY SUPPORTED",
            statusColor = Color(0xFF24B9A6),
            description = "Standard media usage flags permit user-authorized cross-app audio capture."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "Chrome & Web Browsers",
            status = "FULLY SUPPORTED",
            statusColor = Color(0xFF24B9A6),
            description = "Web videos, streaming radio, and video players stream cleanly in sync."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "Mobile Games & Video Players",
            status = "FULLY SUPPORTED",
            statusColor = Color(0xFF24B9A6),
            description = "Low-latency 30ms alignment ensures game audio and local videos remain in sync."
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppCompatibilityRow(
            appName = "DRM-Protected Movies (Netflix, Prime)",
            status = "RESTRICTED BY OS",
            statusColor = Color(0xFFE25241),
            description = "Android OS blanks audio capture when source applications enforce FLAG_SECURE."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Diagnostics Quick Access
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(8.dp))
                .clickable { onOpenDiagnostics() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Technical Diagnostics & Telemetry",
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = WePlayTheme.colors.textPrimary
                    )
                    Text(
                        text = "Inspect jitter, packet loss, RTT, buffer depth, and clock offset",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary
                    )
                }
                Text(
                    text = "VIEW ›",
                    style = WePlayTheme.typography.labelSmall,
                    color = Color(0xFF24B9A6)
                )
            }
        }
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
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
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
                    .background(if (isGranted) Color(0xFF24B9A6).copy(alpha = 0.15f) else Color(0xFFE25241).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGranted) "✓" else "✕",
                    style = WePlayTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (isGranted) Color(0xFF24B9A6) else Color(0xFFE25241)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
            Text(
                text = if (isGranted) "READY" else "NEEDED",
                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                color = if (isGranted) Color(0xFF24B9A6) else Color(0xFFE25241)
            )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(WePlayTheme.colors.surface)
            .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appName,
                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = status,
                        style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                        color = statusColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = WePlayTheme.colors.textSecondary
            )
        }
    }
}
