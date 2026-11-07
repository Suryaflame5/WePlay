package com.weplay.sync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary

/**
 * WePlay Master 4-Pillar Glowing Sound Mark.
 * The primary brand glyph matching the app icon identity (#21CDB8).
 * 4 rounded vertical pillars with vibrant electric teal-to-cyan gradient and soft corona glow.
 */
@Composable
fun WePlayMark(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    tintPrimary: Color = WePlayTealGlow,
    tintSecondary: Color = WePlayTealPrimary,
    glowAlpha: Float = 0.45f,
    waveformProgress: Float = 1.0f,
    playProgress: Float = 1.0f,
    orbitProgress: Float = 1.0f,
    nodesIlluminated: Int = 3
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 4 Pillars relative heights and horizontal positions
        // Outer pillars: 55% height, Inner pillars: 95% height
        val pillarWidth = w * 0.155f
        val cornerRadius = CornerRadius(pillarWidth * 0.5f, pillarWidth * 0.5f)
        val spacing = w * 0.095f

        val totalPillarsWidth = (4 * pillarWidth) + (3 * spacing)
        val startX = (w - totalPillarsWidth) * 0.5f
        val centerY = h * 0.5f

        val heights = listOf(
            h * 0.52f * waveformProgress,
            h * 0.94f * waveformProgress,
            h * 0.94f * waveformProgress,
            h * 0.52f * waveformProgress
        )

        val gradient = Brush.verticalGradient(
            colors = listOf(
                tintPrimary,
                tintSecondary
            ),
            startY = 0f,
            endY = h
        )

        // Draw soft ambient halo behind bars if glowAlpha > 0
        if (glowAlpha > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tintSecondary.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.65f
                )
            )
        }

        // Draw each pillar
        heights.forEachIndexed { i, barH ->
            val x = startX + i * (pillarWidth + spacing)
            val y = centerY - (barH * 0.5f)

            // Outer soft glow stroke
            if (glowAlpha > 0f) {
                drawRoundRect(
                    color = tintSecondary.copy(alpha = glowAlpha * 0.35f),
                    topLeft = Offset(x - 1.5f, y - 1.5f),
                    size = Size(pillarWidth + 3f, barH + 3f),
                    cornerRadius = CornerRadius((pillarWidth + 3f) * 0.5f, (pillarWidth + 3f) * 0.5f)
                )
            }

            // Core pillar
            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(pillarWidth, barH),
                cornerRadius = cornerRadius
            )
        }
    }
}
