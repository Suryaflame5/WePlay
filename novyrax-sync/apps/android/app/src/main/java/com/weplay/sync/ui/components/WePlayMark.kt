package com.weplay.sync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * WePlay Crisp Scalable Vector Mark.
 * Combines a directional play wedge, 5 balanced hi-fi waveform bars,
 * and an orbit path illuminating 3 output nodes (one source to multiple devices).
 */
@Composable
fun WePlayMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tintPlay: Color = WePlayTheme.colors.textPrimary,
    tintAccent: Color = WePlayTheme.colors.accent,
    waveformProgress: Float = 1.0f,
    playProgress: Float = 1.0f,
    orbitProgress: Float = 1.0f,
    nodesIlluminated: Int = 3
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val scale = w / 48f

        // 1. Draw continuous orbit line (tilted -22 degrees)
        if (orbitProgress > 0f) {
            rotate(degrees = -22f, pivot = Offset(w * 0.5f, h * 0.5f)) {
                val rx = 21f * scale
                val ry = 7.5f * scale
                val cx = w * 0.5f
                val cy = h * 0.5f

                val orbitPath = Path().apply {
                    val steps = 60
                    val maxStep = (steps * orbitProgress.coerceIn(0f, 1f)).toInt()
                    for (i in 0..maxStep) {
                        val theta = (i.toFloat() / steps) * (2 * Math.PI)
                        val px = cx + (rx * cos(theta)).toFloat()
                        val py = cy + (ry * sin(theta)).toFloat()
                        if (i == 0) moveTo(px, py) else lineTo(px, py)
                    }
                }

                drawPath(
                    path = orbitPath,
                    color = tintAccent.copy(alpha = 0.85f * orbitProgress),
                    style = Stroke(width = 1.6f * scale, cap = StrokeCap.Round)
                )

                // 3 Output Nodes on Orbit
                val nodeAngles = listOf(0.45 * Math.PI, 1.15 * Math.PI, 1.85 * Math.PI)
                nodeAngles.forEachIndexed { index, angle ->
                    if (index < nodesIlluminated) {
                        val nx = cx + (rx * cos(angle)).toFloat()
                        val ny = cy + (ry * sin(angle)).toFloat()
                        drawCircle(
                            color = tintAccent,
                            radius = 2.4f * scale,
                            center = Offset(nx, ny)
                        )
                    }
                }
            }
        }

        // 2. Center Play Wedge
        if (playProgress > 0f) {
            val playPath = Path().apply {
                moveTo(17.0f * scale, 13.5f * scale)
                lineTo(35.2f * scale, 24.0f * scale)
                lineTo(17.0f * scale, 34.5f * scale)
                close()
            }
            drawPath(
                path = playPath,
                color = tintPlay.copy(alpha = playProgress.coerceIn(0f, 1f))
            )
        }

        // 3. 5 Balanced Waveform Bars
        if (waveformProgress > 0f) {
            val barData = listOf(
                Pair(21.0f, 6.0f),
                Pair(24.0f, 12.0f),
                Pair(27.0f, 16.0f),
                Pair(30.0f, 12.0f),
                Pair(33.0f, 6.0f)
            )

            barData.forEach { (x, maxH) ->
                val currentH = maxH * waveformProgress
                val cy = 24.0f
                drawLine(
                    color = tintAccent,
                    start = Offset(x * scale, (cy - currentH * 0.5f) * scale),
                    end = Offset(x * scale, (cy + currentH * 0.5f) * scale),
                    strokeWidth = 1.8f * scale,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
