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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.components.WePlayBottomNav
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 9: Master "Settings" Screen.
 * Exact visual match to Screen 9 in the design reference:
 * - Top Bar: "Settings" + search magnifying glass
 * - User Profile Card: Cosmic avatar circle + "Nova User" + "nova@weplay.app" + chevron ">"
 * - Section "Audio Settings":
 *     1. Output Routing (chevron ">")
 *     2. Latency Optimization ("Auto >")
 *     3. Audio Quality ("Hi-Res >")
 * - Section "General":
 *     1. Appearance ("System >")
 *     2. Notifications (chevron ">")
 *     3. About WePlay (chevron ">")
 */
@Composable
fun SettingsCompatibilityScreen(
    onOpenDiagnostics: () -> Unit,
    onBack: () -> Unit,
    onNavigateToTab: (AppScreen) -> Unit = {},
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
                .padding(horizontal = 22.dp, vertical = 14.dp)
                .padding(bottom = 90.dp)
        ) {
            // 1. Top Bar: "Settings" & Search Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Settings",
                    tint = WePlayTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. User Profile Card ("Nova User" / "nova@weplay.app")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlaySurface)
                    .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Cosmic Avatar Glyph
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6), Color(0xFFFF6B00))
                                    )
                                )
                                .border(1.5.dp, WePlayOrangeGlow.copy(alpha = 0.7f), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Nova User",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WePlayTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "nova@weplay.app",
                                fontSize = 12.sp,
                                color = WePlayTextSecondary
                            )
                        }
                    }

                    WePlayIcon(
                        type = WePlayIconType.CHEVRON_RIGHT,
                        tint = WePlayTextMuted,
                        size = 18.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 3. Section: "Audio Settings"
            Text(
                text = "Audio Settings",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = WePlayTextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlaySurface)
                    .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
            ) {
                Column {
                    SettingsRowItem(
                        iconType = WePlayIconType.ROUTE,
                        title = "Output Routing",
                        value = null,
                        onClick = onOpenDiagnostics,
                        showDivider = true
                    )
                    SettingsRowItem(
                        iconType = WePlayIconType.SPEED,
                        title = "Latency Optimization",
                        value = "Auto",
                        onClick = onOpenDiagnostics,
                        showDivider = true
                    )
                    SettingsRowItem(
                        iconType = WePlayIconType.HI_RES,
                        title = "Audio Quality",
                        value = "Hi-Res",
                        onClick = onOpenDiagnostics,
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 4. Section: "General"
            Text(
                text = "General",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = WePlayTextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WePlaySurface)
                    .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
            ) {
                Column {
                    SettingsRowItem(
                        iconType = WePlayIconType.DISCOVERY_RADAR,
                        title = "Appearance",
                        value = "System",
                        onClick = { },
                        showDivider = true
                    )
                    SettingsRowSimple(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        value = null,
                        onClick = { },
                        showDivider = true
                    )
                    SettingsRowSimple(
                        icon = Icons.Default.Info,
                        title = "About WePlay",
                        value = null,
                        onClick = onOpenDiagnostics,
                        showDivider = false
                    )
                }
            }
        }

        // Docked Bottom Navigation Bar
        WePlayBottomNav(
            currentTab = AppScreen.SETTINGS,
            onTabSelect = onNavigateToTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SettingsRowItem(
    iconType: WePlayIconType,
    title: String,
    value: String?,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WePlayIcon(
                    type = iconType,
                    tint = WePlayTextSecondary,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = WePlayTextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        color = WePlayTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                WePlayIcon(
                    type = WePlayIconType.CHEVRON_RIGHT,
                    tint = WePlayTextMuted,
                    size = 16.dp
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(WePlayBorderSubtle)
            )
        }
    }
}

@Composable
private fun SettingsRowSimple(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String?,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = WePlayTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = WePlayTextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        color = WePlayTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                WePlayIcon(
                    type = WePlayIconType.CHEVRON_RIGHT,
                    tint = WePlayTextMuted,
                    size = 16.dp
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(WePlayBorderSubtle)
            )
        }
    }
}
