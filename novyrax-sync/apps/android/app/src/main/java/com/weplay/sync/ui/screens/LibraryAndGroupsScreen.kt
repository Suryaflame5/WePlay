package com.weplay.sync.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.DeviceGroup
import com.weplay.sync.ui.components.WePlayBottomNav
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

data class LibraryTrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationFormatted: String,
    val format: String = "FLAC 24-bit / 96k"
)

/**
 * Screen 7: Master "Your Library" Screen.
 * Exact visual match to Screen 7 in the design reference:
 * - Top Bar: "Your Library" + menu list icon
 * - Pill Search Box: "Search music, artists..."
 * - Category Filter Pills: "All" (orange active), "Albums", "Playlists", "Artists"
 * - 2x2 Grid of 3D visual cards:
 *     1. Ambient Collection (128 tracks)
 *     2. Focus Beats (87 tracks)
 *     3. Chill Vibes (64 tracks)
 *     4. LoFi Dreams (102 tracks)
 * - Docked Mini-Player above bottom nav: "Midnight Horizons / Ambient Atlas" + pause button
 * - Docked Bottom Nav with "Library" tab active
 */
@Composable
fun LibraryAndGroupsScreen(
    libraryTracks: List<LibraryTrackItem>,
    savedGroups: List<DeviceGroup>,
    onTrackSelect: (LibraryTrackItem) -> Unit,
    onApplyGroup: (DeviceGroup) -> Unit,
    onSaveCurrentGroup: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

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
                .padding(bottom = 150.dp)
        ) {
            // 1. Top Bar: "Your Library" + Menu Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WePlayTextPrimary
                )

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = WePlayTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Search Bar Capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color(0xFF101520))
                    .border(1.dp, WePlayBorder, RoundedCornerShape(23.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = WePlayTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = WePlayTextPrimary,
                            fontSize = 14.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(WePlayOrangePrimary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search music, artists...",
                                    fontSize = 14.sp,
                                    color = WePlayTextMuted
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Category Filter Chips (Horizontal Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filters = listOf("All", "Albums", "Playlists", "Artists")
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) WePlayOrangePrimary else WePlaySurface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) WePlayOrangeGlow else WePlayBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else WePlayTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. 2x2 Artwork Grid (Ambient Collection, Focus Beats, Chill Vibes, LoFi Dreams)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Ambient Collection & Focus Beats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    LibraryCard3D(
                        title = "Ambient Collection",
                        trackCount = "128 tracks",
                        theme = "ambient",
                        onClick = {
                            onTrackSelect(LibraryTrackItem("1", "Ambient Flow", "Atlas", "Ambient", "3:40"))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    LibraryCard3D(
                        title = "Focus Beats",
                        trackCount = "87 tracks",
                        theme = "focus",
                        onClick = {
                            onTrackSelect(LibraryTrackItem("2", "Deep Pulse", "Nova", "Focus", "4:12"))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Chill Vibes & LoFi Dreams
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    LibraryCard3D(
                        title = "Chill Vibes",
                        trackCount = "64 tracks",
                        theme = "chill",
                        onClick = {
                            onTrackSelect(LibraryTrackItem("3", "Velvet Night", "Solar", "Chill", "2:55"))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    LibraryCard3D(
                        title = "LoFi Dreams",
                        trackCount = "102 tracks",
                        theme = "lofi",
                        onClick = {
                            onTrackSelect(LibraryTrackItem("4", "Sunken Garden", "Aura", "Dreams", "3:18"))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Docked Sticky Mini-Player + Bottom Navigation
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Mini-Player Docked Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF131926).copy(alpha = 0.95f))
                    .border(1.dp, WePlayBorder, RoundedCornerShape(14.dp))
                    .clickable { onTrackSelect(LibraryTrackItem("0", "Midnight Horizons", "Ambient Atlas", "Atlas", "3:24")) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mini Album Artwork
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF4A154B), Color(0xFFFF6A3D))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Midnight Horizons",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WePlayTextPrimary
                            )
                            Text(
                                text = "Ambient Atlas",
                                fontSize = 11.sp,
                                color = WePlayTextSecondary
                            )
                        }
                    }

                    // Pause Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WePlaySurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(14.dp)) {
                            val w = size.width
                            val h = size.height
                            drawRoundRect(Color.White, Offset(w * 0.18f, h * 0.15f), Size(w * 0.22f, h * 0.70f), CornerRadius(1.dp.toPx(), 1.dp.toPx()))
                            drawRoundRect(Color.White, Offset(w * 0.60f, h * 0.15f), Size(w * 0.22f, h * 0.70f), CornerRadius(1.dp.toPx(), 1.dp.toPx()))
                        }
                    }
                }
            }

            // Bottom Nav with Library active
            WePlayBottomNav(
                currentTab = AppScreen.LIBRARY,
                onTabSelect = { /* navigate */ }
            )
        }
    }
}

/**
 * 3D Artistic Card for the 2x2 Grid with procedural canvas landscape illustrations.
 */
@Composable
private fun LibraryCard3D(
    title: String,
    trackCount: String,
    theme: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(WePlaySurface)
            .border(1.dp, WePlayBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Procedural Artwork on Top Half
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            val gradientColors = when (theme) {
                "ambient" -> listOf(Color(0xFF0C1E3A), Color(0xFF1E40AF), Color(0xFF38BDF8))
                "focus" -> listOf(Color(0xFF2E1065), Color(0xFF7E22CE), Color(0xFFEC4899))
                "chill" -> listOf(Color(0xFF064E3B), Color(0xFF0D9488), Color(0xFF2DD4BF))
                else -> listOf(Color(0xFF4C0519), Color(0xFF9F1239), Color(0xFFFB7185))
            }

            // Background art gradient
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = h * 0.65f
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, h * 0.65f),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            // Dynamic 3D shapes inside artwork
            when (theme) {
                "ambient" -> {
                    // Soft blue mountain peaks
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, h * 0.65f)
                        lineTo(w * 0.3f, h * 0.35f)
                        lineTo(w * 0.6f, h * 0.55f)
                        lineTo(w, h * 0.25f)
                        lineTo(w, h * 0.65f)
                        close()
                    }
                    drawPath(path, Color(0xFF0F172A).copy(alpha = 0.5f))
                }
                "focus" -> {
                    // Iridescent sphere
                    drawCircle(
                        color = Color(0xFFF472B6).copy(alpha = 0.6f),
                        radius = w * 0.25f,
                        center = Offset(w * 0.5f, h * 0.32f)
                    )
                }
                "chill" -> {
                    // Moon & wave
                    drawCircle(
                        color = Color(0xFFA7F3D0).copy(alpha = 0.7f),
                        radius = w * 0.18f,
                        center = Offset(w * 0.65f, h * 0.25f)
                    )
                }
                else -> {
                    // Geometric grid lines
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(0f, h * 0.4f),
                        end = Offset(w, h * 0.4f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            // Dark bottom container overlay for text
            drawRect(
                color = WePlaySurface,
                topLeft = Offset(0f, h * 0.62f),
                size = Size(w, h * 0.38f)
            )
        }

        // Card Text Content (Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WePlayTextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trackCount,
                fontSize = 11.sp,
                color = WePlayTextSecondary
            )
        }
    }
}
