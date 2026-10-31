package com.weplay.sync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.weplay.sync.ui.theme.WePlayTheme

data class AlbumArtPalette(
    val dominantAccent: Color,
    val secondaryAccent: Color,
    val ambientGlow: Color
)

object ArtworkPalettes {
    val Charcoal = AlbumArtPalette(
        dominantAccent = Color(0xFF24B9A6), // Atelier Teal
        secondaryAccent = Color(0xFF5878D9), // Blue
        ambientGlow = Color(0x2E24B9A6)
    )

    val Solitude = AlbumArtPalette(
        dominantAccent = Color(0xFFF27B58), // Atelier Coral
        secondaryAccent = Color(0xFFD0A34A), // Gold
        ambientGlow = Color(0x28F27B58)
    )

    val DarkAtmosphere = AlbumArtPalette(
        dominantAccent = Color(0xFF9A86C8), // Atelier Lavender
        secondaryAccent = Color(0xFF5878D9), // Blue
        ambientGlow = Color(0x289A86C8)
    )
}

@Composable
fun ArtworkAmbientBackdrop(
    palette: AlbumArtPalette,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val canvas = WePlayTheme.colors.canvas
    val isDark = WePlayTheme.colors.isDark

    val glowAlpha = if (isDark) 0.35f else 0.18f
    val subtleGlow = palette.dominantAccent.copy(alpha = glowAlpha)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvas)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        subtleGlow,
                        canvas.copy(alpha = 0.85f),
                        canvas
                    )
                )
            )
    ) {
        content()
    }
}
