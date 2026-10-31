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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.ui.components.AlbumArtPalette
import com.weplay.sync.ui.components.ArtworkPalettes
import com.weplay.sync.ui.components.WePlayMark
import com.weplay.sync.ui.theme.WePlayTheme

data class FeaturedAlbum(
    val title: String,
    val artist: String,
    val year: String,
    val format: String,
    val palette: AlbumArtPalette,
    val trackCount: Int
)

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
    val featured = FeaturedAlbum(
        title = "Charcoal",
        artist = "Brambles",
        year = "2012",
        format = "FLAC 96kHz / 24-bit",
        palette = ArtworkPalettes.Charcoal,
        trackCount = 8
    )

    val recentAlbums = listOf(
        FeaturedAlbum("Charcoal", "Brambles", "2012", "FLAC 24-bit", ArtworkPalettes.Charcoal, 8),
        FeaturedAlbum("Ambient 1: Music for Airports", "Brian Eno", "1978", "ALAC 24-bit", ArtworkPalettes.Solitude, 4),
        FeaturedAlbum("Ravedeath, 1972", "Tim Hecker", "2011", "FLAC 24-bit", ArtworkPalettes.DarkAtmosphere, 12),
        FeaturedAlbum("Selected Ambient Works", "Aphex Twin", "1992", "WAV 16-bit", ArtworkPalettes.Solitude, 13)
    )

    val activeCount = activeDevices.count { it.isSelectedForPlayback }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayTheme.colors.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp) // Clearance for sticky MiniPlayer
        ) {
            // A. Atelier Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenLibrary() }
                ) {
                    WePlayMark(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WEPLAY",
                            style = WePlayTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                            color = WePlayTheme.colors.textPrimary
                        )
                        Text(
                            text = "ATELIER EDITION",
                            style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp, color = WePlayTheme.colors.textSecondary)
                        )
                    }
                }

                // Audio Sink Status Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(WePlayTheme.colors.surface)
                            .border(1.dp, if (activeCount > 0) WePlayTheme.colors.accent else WePlayTheme.colors.separator, RoundedCornerShape(3.dp))
                            .clickable { onOpenAudioRoom() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (activeCount > 0) WePlayTheme.colors.accent else WePlayTheme.colors.textSecondary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (activeCount == 0) "STANDALONE" else "$activeCount DESTINATIONS",
                                style = WePlayTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (activeCount > 0) WePlayTheme.colors.textPrimary else WePlayTheme.colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Settings Glyph Access
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WePlayTheme.colors.surfaceSubtle)
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚙",
                            style = WePlayTheme.typography.bodyMedium,
                            color = WePlayTheme.colors.textPrimary
                        )
                    }
                }
            }

            // B. Prominent Featured Listening Artwork Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "FEATURED MASTER",
                    style = WePlayTheme.typography.labelSmall.copy(color = WePlayTheme.colors.coral),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    featured.palette.dominantAccent.copy(alpha = 0.22f),
                                    WePlayTheme.colors.surface
                                )
                            )
                        )
                        .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(6.dp))
                        .clickable {
                            onTrackSelect("Unsayable", featured.artist, featured.title)
                            onOpenPlayer()
                        }
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Artwork Vinyl Frame
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(WePlayTheme.colors.surfaceSubtle)
                                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(WePlayTheme.colors.canvas)
                                        .border(1.dp, WePlayTheme.colors.separator, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(featured.palette.dominantAccent.copy(alpha = 0.35f))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = featured.title,
                                    style = WePlayTheme.typography.displayMedium,
                                    color = WePlayTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${featured.artist} • ${featured.year}",
                                    style = WePlayTheme.typography.bodyLarge,
                                    color = WePlayTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = featured.format,
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 10.sp),
                                    color = featured.palette.dominantAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "8 tracks • Pure dynamic range pass-through",
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = WePlayTheme.colors.textSecondary
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(WePlayTheme.colors.textPrimary)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "PLAY ALBUM",
                                    style = WePlayTheme.typography.labelSmall,
                                    color = WePlayTheme.colors.canvas
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // C. Audio Room Real State Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYNCHRONIZED AUDIO ROOM",
                        style = WePlayTheme.typography.labelSmall,
                        color = WePlayTheme.colors.textSecondary
                    )
                    Text(
                        text = "OPEN ROOM >",
                        style = WePlayTheme.typography.labelSmall.copy(color = WePlayTheme.colors.accent),
                        modifier = Modifier.clickable { onOpenAudioRoom() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(WePlayTheme.colors.surface)
                        .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp))
                        .clickable { onOpenAudioRoom() }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (activeCount > 0) "$activeCount Outputs Streaming" else "No Output Sinks Bound",
                                    style = WePlayTheme.typography.titleMedium,
                                    color = WePlayTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (activeCount > 0) "Living Room Arc (+2.4ms) • Kitchen Move (+4.1ms)" else "Tap to add Bluetooth or Wi-Fi receivers",
                                    style = WePlayTheme.typography.bodyMedium,
                                    color = WePlayTheme.colors.textSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(WePlayTheme.colors.accent.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ROOM CALIBRATED",
                                    style = WePlayTheme.typography.tabularMetric.copy(fontSize = 9.sp),
                                    color = WePlayTheme.colors.accent
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // D. Continue Listening Editorial Carousel
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CONTINUE LISTENING",
                    style = WePlayTheme.typography.labelSmall,
                    color = WePlayTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
                ) {
                    items(recentAlbums) { album ->
                        Column(
                            modifier = Modifier
                                .width(136.dp)
                                .clickable {
                                    onTrackSelect("Track 1", album.artist, album.title)
                                    onOpenPlayer()
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(136.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                album.palette.dominantAccent.copy(alpha = 0.3f),
                                                WePlayTheme.colors.surface
                                            )
                                        )
                                    )
                                    .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = album.title.take(1),
                                    style = WePlayTheme.typography.displayLarge.copy(fontSize = 36.sp),
                                    color = album.palette.dominantAccent.copy(alpha = 0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = album.title,
                                style = WePlayTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                color = WePlayTheme.colors.textPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = album.artist,
                                style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = WePlayTheme.colors.textSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // E. Quick Access to Lossless Library
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(WePlayTheme.colors.surfaceSubtle)
                        .clickable { onOpenLibrary() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lossless Music Library",
                            style = WePlayTheme.typography.titleMedium,
                            color = WePlayTheme.colors.textPrimary
                        )
                        Text(
                            text = "5 FLAC 24-bit masters indexed on local storage",
                            style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = WePlayTheme.colors.textSecondary
                        )
                    }
                    Text(
                        text = "BROWSE >",
                        style = WePlayTheme.typography.labelSmall,
                        color = WePlayTheme.colors.textPrimary
                    )
                }
            }
        }

        // F. Persistent Bottom Floating Mini-Player (Atelier Capsule)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(WePlayTheme.colors.surface)
                .border(1.dp, WePlayTheme.colors.separator, RoundedCornerShape(28.dp))
                .clickable { onOpenPlayer() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Artwork disc
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.surfaceSubtle)
                        .border(1.dp, WePlayTheme.colors.separator, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(WePlayTheme.colors.accent)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trackState.title,
                        style = WePlayTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = WePlayTheme.colors.textPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${trackState.artist} • ${trackState.format} 96k",
                        style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = WePlayTheme.colors.textSecondary,
                        maxLines = 1
                    )
                }

                // Mini Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.textPrimary)
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPlaying) "⏸" else "▶",
                        style = WePlayTheme.typography.bodyMedium,
                        color = WePlayTheme.colors.canvas
                    )
                }
            }
        }
    }
}
