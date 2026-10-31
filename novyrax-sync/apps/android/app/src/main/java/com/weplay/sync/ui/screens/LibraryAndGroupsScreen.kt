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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var selectedTab by remember { mutableStateOf(0) } // 0 = Tracks, 1 = Saved Rooms, 2 = Local Storage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = "‹ ATELIER",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "EDITORIAL COLLECTION",
                style = WePlayTheme.typography.labelSmall,
                color = WePlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Atelier Mode Switcher (Clean, sharp tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(3.dp))
                .clip(RoundedCornerShape(3.dp))
        ) {
            listOf("TRACKS", "SAVED ROOMS", "STORAGE").forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selectedTab == index) WePlayTheme.colors.textPrimary else WePlayTheme.colors.surface)
                        .clickable { selectedTab = index }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = WePlayTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (selectedTab == index) WePlayTheme.colors.canvas else WePlayTheme.colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (selectedTab) {
            0 -> {
                // TRACKS LIST
                Text(
                    text = "${libraryTracks.size} Lossless FLAC Masters",
                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 20.sp),
                    color = WePlayTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Indexed directly from on-device storage. No cloud dependencies.",
                    style = WePlayTheme.typography.bodyMedium,
                    color = WePlayTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(libraryTracks, key = { it.id }) { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(3.dp))
                                .background(WePlayTheme.colors.surface)
                                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(3.dp))
                                .clickable { onTrackSelect(track) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = WePlayTheme.typography.titleMedium,
                                    color = WePlayTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${track.artist} • ${track.album}",
                                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                    color = WePlayTheme.colors.textSecondary
                                )
                            }
                            Text(
                                text = track.format,
                                style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                                color = WePlayTheme.colors.accent
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = track.durationFormatted,
                                style = WePlayTheme.typography.tabularMetric,
                                color = WePlayTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            1 -> {
                // SAVED ROOMS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROOM PRESETS",
                        style = WePlayTheme.typography.labelSmall,
                        color = WePlayTheme.colors.textSecondary
                    )
                    Box(
                        modifier = Modifier
                            .clickable { onSaveCurrentGroup() }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "+ SAVE SETUP",
                            style = WePlayTheme.typography.labelSmall,
                            color = WePlayTheme.colors.accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedGroups, key = { it.id }) { group ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(WePlayTheme.colors.surface)
                                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = group.name,
                                    style = WePlayTheme.typography.titleMedium,
                                    color = WePlayTheme.colors.textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(WePlayTheme.colors.textPrimary)
                                        .clickable { onApplyGroup(group) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "BIND SINK",
                                        style = WePlayTheme.typography.labelSmall,
                                        color = WePlayTheme.colors.canvas
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${group.deviceIds.size} designated audio sinks configured",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = WePlayTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            2 -> {
                // STORAGE & LOCAL-FIRST
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Local Storage & Zero Telemetry",
                        style = WePlayTheme.typography.titleLarge,
                        color = WePlayTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "WePlay runs entirely on-device decoders and local UDP socket transports. No accounts, listening telemetry, or media streams are relayed to cloud servers.",
                        style = WePlayTheme.typography.bodyMedium,
                        color = WePlayTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Dynamic Buffer Re-alignment",
                        style = WePlayTheme.typography.titleMedium,
                        color = WePlayTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "WePlay continuously computes round-trip delay and clock offsets (θ) using SNTP protocol over Wi-Fi companion receivers.",
                        style = WePlayTheme.typography.bodyMedium,
                        color = WePlayTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}
