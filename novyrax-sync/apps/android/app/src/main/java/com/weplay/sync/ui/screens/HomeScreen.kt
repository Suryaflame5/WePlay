package com.weplay.sync.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.DeviceGroup

/**
 * HomeScreen maps cleanly to the Master "Your Library" experience.
 */
@Composable
fun HomeScreen(
    trackState: PlayerTrackState,
    activeDevices: List<AudioOutputDevice>,
    isPlaying: Boolean,
    onTrackSelect: (String, String, String) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onOpenPlayer: () -> Unit,
    onOpenAudioRoom: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraryTracks = remember {
        listOf(
            LibraryTrackItem("1", "Midnight Horizons", "Ambient Atlas", "Atlas", "3:24"),
            LibraryTrackItem("2", "Ambient Flow", "Atlas", "Ambient", "3:40"),
            LibraryTrackItem("3", "Deep Pulse", "Nova", "Focus", "4:12"),
            LibraryTrackItem("4", "Velvet Night", "Solar", "Chill", "2:55")
        )
    }

    LibraryAndGroupsScreen(
        libraryTracks = libraryTracks,
        savedGroups = emptyList(),
        onTrackSelect = { item ->
            onTrackSelect(item.title, item.artist, item.album)
            onOpenPlayer()
        },
        onApplyGroup = { },
        onSaveCurrentGroup = { },
        onBack = { },
        modifier = modifier
    )
}
