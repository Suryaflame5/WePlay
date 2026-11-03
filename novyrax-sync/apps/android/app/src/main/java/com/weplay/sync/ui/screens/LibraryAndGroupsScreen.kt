package com.weplay.sync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.DeviceGroup
import com.weplay.sync.ui.theme.WePlayTheme

data class LibraryTrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationFormatted: String,
    val format: String = "FLAC 24-bit / 96k"
)

@Composable
fun LibraryAndGroupsScreen(
    libraryTracks: List<LibraryTrackItem>,
    savedGroups: List<DeviceGroup>,
    onTrackSelect: (LibraryTrackItem) -> Unit,
    onApplyGroup: (DeviceGroup) -> Unit,
    onSaveCurrentGroup: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = CURATED ALBUMS, 1 = TRACKS, 2 = SAVED ROOMS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F14))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 10.dp)
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
                text = "CURATED AUDIO COLLECTION",
                style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.2.sp),
                color = Color(0xFF00E5C8)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF141922))
                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(6.dp))
                .padding(3.dp)
        ) {
            listOf("CURATED ALBUMS", "FLAC TRACKS", "ROOM PRESETS").forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) Color(0xFF00E5C8) else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = WePlayTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = if (isSelected) Color(0xFF0A0D12) else Color(0xFF94A3B8)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // CURATED ALBUMS: Rich visual cards (Image 1 & 2 reference)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        CuratedAlbumCard(
                            title = "Charcoal",
                            artist = "Brambles",
                            year = "2012",
                            format = "FLAC 96kHz / 24-bit",
                            accentColor = Color(0xFFF27B58),
                            description = "Atmospheric acoustic textures, gentle piano and decaying woodcut ambient.",
                            onPlay = {
                                libraryTracks.firstOrNull()?.let { onTrackSelect(it) }
                            }
                        )
                    }

                    item {
                        CuratedAlbumCard(
                            title = "Ambient 1: Music for Airports",
                            artist = "Brian Eno",
                            year = "1978",
                            format = "ALAC 192kHz / 24-bit",
                            accentColor = Color(0xFF5878D9),
                            description = "Landmark minimalist sound installations engineered for acoustic reflection.",
                            onPlay = {
                                libraryTracks.firstOrNull()?.let { onTrackSelect(it) }
                            }
                        )
                    }

                    item {
                        CuratedAlbumCard(
                            title = "Ravedeath, 1972",
                            artist = "Tim Hecker",
                            year = "2011",
                            format = "FLAC 48kHz / 24-bit",
                            accentColor = Color(0xFF9A86C8),
                            description = "Pipe organs, tape decay, and spatial resonance recorded inside Reykjavík cathedral.",
                            onPlay = {
                                libraryTracks.firstOrNull()?.let { onTrackSelect(it) }
                            }
                        )
                    }
                }
            }

            1 -> {
                // TRACKS LIST
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(libraryTracks, key = { it.id }) { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF141922))
                                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(6.dp))
                                .clickable { onTrackSelect(track) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1C2430)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "▶",
                                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 12.sp),
                                    color = Color(0xFF00E5C8)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                    color = Color(0xFFF1F5F9)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${track.artist} • ${track.album}",
                                    style = WePlayTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF777D86)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF1C2533))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = track.format,
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                                    color = Color(0xFF00E5C8)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = track.durationFormatted,
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            2 -> {
                // ROOM PRESETS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVED SOUNDSTAGE PRESETS",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        color = Color(0xFF777D86)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF00E5C8))
                            .clickable { onSaveCurrentGroup() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+ SAVE PRESET",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color(0xFF0B1017)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedGroups, key = { it.id }) { group ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141922))
                                .border(1.dp, Color(0xFF222B38), RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = group.name,
                                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = Color(0xFFF1F5F9)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${group.deviceIds.size} designated audio sinks configured",
                                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF777D86)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF1E2836))
                                        .border(1.dp, Color(0xFF00E5C8), RoundedCornerShape(4.dp))
                                        .clickable { onApplyGroup(group) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "BIND SINK",
                                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = Color(0xFF00E5C8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CuratedAlbumCard(
    title: String,
    artist: String,
    year: String,
    format: String,
    accentColor: Color,
    description: String,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(accentColor.copy(alpha = 0.15f), Color(0xFF131720))
                )
            )
            .border(1.dp, Color(0xFF232D3B), RoundedCornerShape(12.dp))
            .clickable { onPlay() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vinyl / Album Artwork Mockup
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0E1217))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A222E))
                            .border(1.dp, Color(0xFF334155), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = Color(0xFFF1F5F9)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$artist • $year",
                        style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E2836))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = format,
                            style = WePlayTheme.typography.tabularMetric.copy(fontSize = 8.sp),
                            color = Color(0xFF00E5C8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = WePlayTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = Color(0xFF777D86)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF00E5C8))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PLAY MASTER",
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                        color = Color(0xFF0A0D12)
                    )
                }
            }
        }
    }
}
