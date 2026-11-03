package com.weplay.sync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WePlayIconType {
    HEADPHONES,
    SPEAKER,
    ROOM,
    LIBRARY,
    ROUTE,
    EQUALIZER,
    SPEED,
    HI_RES,
    CROP_RETICLE,
    CANCEL,
    DISCOVERY_RADAR,
    CHEVRON_RIGHT,
    AURACAST
}

/**
 * High-precision vector glyphs for audio instruments & device types.
 * Pure vector canvas with crisp anti-aliased geometry.
 */
@Composable
fun WePlayIcon(
    type: WePlayIconType,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = (w * 0.08f).coerceAtLeast(1.2f)

        when (type) {
            WePlayIconType.HEADPHONES -> {
                // Headband arch
                val archRadius = w * 0.38f
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.5f - archRadius, h * 0.15f),
                    size = Size(archRadius * 2f, archRadius * 1.6f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                // Left Ear Cup
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.45f),
                    size = Size(w * 0.18f, h * 0.38f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )
                // Right Ear Cup
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.70f, h * 0.45f),
                    size = Size(w * 0.18f, h * 0.38f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )
            }

            WePlayIconType.SPEAKER -> {
                // Speaker Cabinet
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.20f, h * 0.10f),
                    size = Size(w * 0.60f, h * 0.80f),
                    cornerRadius = CornerRadius(w * 0.10f, w * 0.10f),
                    style = Stroke(width = stroke)
                )
                // Tweeter
                drawCircle(
                    color = tint,
                    radius = w * 0.09f,
                    center = Offset(w * 0.5f, h * 0.32f),
                    style = Stroke(width = stroke * 0.8f)
                )
                // Woofer
                drawCircle(
                    color = tint,
                    radius = w * 0.18f,
                    center = Offset(w * 0.5f, h * 0.64f),
                    style = Stroke(width = stroke)
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.06f,
                    center = Offset(w * 0.5f, h * 0.64f)
                )
            }

            WePlayIconType.ROOM -> {
                // Room / Doorframe box
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.15f, h * 0.15f),
                    size = Size(w * 0.70f, h * 0.70f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = stroke)
                )
                // Inner door
                drawLine(
                    color = tint,
                    start = Offset(w * 0.45f, h * 0.15f),
                    end = Offset(w * 0.45f, h * 0.85f),
                    strokeWidth = stroke
                )
                // Doorknob dot
                drawCircle(
                    color = tint,
                    radius = w * 0.04f,
                    center = Offset(w * 0.38f, h * 0.52f)
                )
            }

            WePlayIconType.LIBRARY -> {
                // 3 Stacked Library / Playlist Bars
                drawLine(tint, Offset(w * 0.18f, h * 0.25f), Offset(w * 0.82f, h * 0.25f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.18f, h * 0.50f), Offset(w * 0.82f, h * 0.50f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.18f, h * 0.75f), Offset(w * 0.58f, h * 0.75f), stroke, StrokeCap.Round)
                // Play triangle on bottom right
                val playPath = Path().apply {
                    moveTo(w * 0.68f, h * 0.65f)
                    lineTo(w * 0.84f, h * 0.75f)
                    lineTo(w * 0.68f, h * 0.85f)
                    close()
                }
                drawPath(playPath, tint)
            }

            WePlayIconType.ROUTE -> {
                // Route splitting path
                val routePath = Path().apply {
                    moveTo(w * 0.20f, h * 0.50f)
                    lineTo(w * 0.50f, h * 0.50f)
                    cubicTo(w * 0.65f, h * 0.50f, w * 0.65f, h * 0.25f, w * 0.80f, h * 0.25f)
                    moveTo(w * 0.50f, h * 0.50f)
                    cubicTo(w * 0.65f, h * 0.50f, w * 0.65f, h * 0.75f, w * 0.80f, h * 0.75f)
                }
                drawPath(routePath, tint, style = Stroke(width = stroke, cap = StrokeCap.Round))
                // Nodes
                drawCircle(tint, w * 0.08f, Offset(w * 0.20f, h * 0.50f))
                drawCircle(tint, w * 0.08f, Offset(w * 0.80f, h * 0.25f))
                drawCircle(tint, w * 0.08f, Offset(w * 0.80f, h * 0.75f))
            }

            WePlayIconType.EQUALIZER -> {
                // 3 Equalizer sliders
                // Bar 1
                drawLine(tint, Offset(w * 0.25f, h * 0.15f), Offset(w * 0.25f, h * 0.85f), stroke * 0.8f, StrokeCap.Round)
                drawCircle(tint, w * 0.10f, Offset(w * 0.25f, h * 0.40f))

                // Bar 2
                drawLine(tint, Offset(w * 0.50f, h * 0.15f), Offset(w * 0.50f, h * 0.85f), stroke * 0.8f, StrokeCap.Round)
                drawCircle(tint, w * 0.10f, Offset(w * 0.50f, h * 0.65f))

                // Bar 3
                drawLine(tint, Offset(w * 0.75f, h * 0.15f), Offset(w * 0.75f, h * 0.85f), stroke * 0.8f, StrokeCap.Round)
                drawCircle(tint, w * 0.10f, Offset(w * 0.75f, h * 0.30f))
            }

            WePlayIconType.SPEED -> {
                // Speedometer gauge arc
                drawArc(
                    color = tint,
                    startAngle = 145f,
                    sweepAngle = 250f,
                    useCenter = false,
                    topLeft = Offset(w * 0.15f, h * 0.15f),
                    size = Size(w * 0.70f, h * 0.70f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                // Needle
                drawLine(
                    color = tint,
                    start = Offset(w * 0.50f, h * 0.55f),
                    end = Offset(w * 0.68f, h * 0.35f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }

            WePlayIconType.HI_RES -> {
                // Hi-Res badge rounded square
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.18f),
                    size = Size(w * 0.76f, h * 0.64f),
                    cornerRadius = CornerRadius(w * 0.10f, w * 0.10f),
                    style = Stroke(width = stroke)
                )
                // "HR" indication line
                drawLine(tint, Offset(w * 0.30f, h * 0.38f), Offset(w * 0.30f, h * 0.62f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.30f, h * 0.50f), Offset(w * 0.44f, h * 0.50f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.44f, h * 0.38f), Offset(w * 0.44f, h * 0.62f), stroke, StrokeCap.Round)

                drawLine(tint, Offset(w * 0.56f, h * 0.38f), Offset(w * 0.56f, h * 0.62f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.56f, h * 0.38f), Offset(w * 0.70f, h * 0.46f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.70f, h * 0.46f), Offset(w * 0.56f, h * 0.54f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.62f, h * 0.54f), Offset(w * 0.70f, h * 0.62f), stroke, StrokeCap.Round)
            }

            WePlayIconType.CROP_RETICLE -> {
                val len = w * 0.28f
                // Top-left
                drawLine(tint, Offset(0f, 0f), Offset(len, 0f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(0f, 0f), Offset(0f, len), stroke, StrokeCap.Round)
                // Top-right
                drawLine(tint, Offset(w, 0f), Offset(w - len, 0f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w, 0f), Offset(w, len), stroke, StrokeCap.Round)
                // Bottom-left
                drawLine(tint, Offset(0f, h), Offset(len, h), stroke, StrokeCap.Round)
                drawLine(tint, Offset(0f, h), Offset(0f, h - len), stroke, StrokeCap.Round)
                // Bottom-right
                drawLine(tint, Offset(w, h), Offset(w - len, h), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w, h), Offset(w, h - len), stroke, StrokeCap.Round)
            }

            WePlayIconType.CANCEL -> {
                drawCircle(tint.copy(alpha = 0.35f), radius = w * 0.45f, center = Offset(w * 0.5f, h * 0.5f))
                drawLine(tint, Offset(w * 0.32f, h * 0.32f), Offset(w * 0.68f, h * 0.68f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.68f, h * 0.32f), Offset(w * 0.32f, h * 0.68f), stroke, StrokeCap.Round)
            }

            WePlayIconType.DISCOVERY_RADAR -> {
                drawCircle(tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = stroke))
                drawCircle(tint, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = stroke))
                drawCircle(tint, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.5f))
            }

            WePlayIconType.CHEVRON_RIGHT -> {
                val path = Path().apply {
                    moveTo(w * 0.35f, h * 0.20f)
                    lineTo(w * 0.65f, h * 0.50f)
                    lineTo(w * 0.35f, h * 0.80f)
                }
                drawPath(path, tint, style = Stroke(width = stroke * 1.2f, cap = StrokeCap.Round))
            }

            WePlayIconType.AURACAST -> {
                // Radio antenna beacon with concentric broadcasting arcs
                drawLine(tint, Offset(w * 0.50f, h * 0.45f), Offset(w * 0.50f, h * 0.88f), stroke * 1.1f, StrokeCap.Round)
                drawCircle(tint, radius = w * 0.12f, center = Offset(w * 0.50f, h * 0.45f))
                drawArc(
                    color = tint,
                    startAngle = 215f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.15f),
                    size = Size(w * 0.56f, w * 0.56f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = tint,
                    startAngle = 215f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(w * 0.08f, h * 0.02f),
                    size = Size(w * 0.84f, w * 0.84f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }
    }
}
