package com.weplay.sync.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.screens.AppScreen
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayElectricBlue
import com.weplay.sync.ui.theme.WePlayElectricCyan
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeDark
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlaySurface
import com.weplay.sync.ui.theme.WePlaySurfaceElevated
import com.weplay.sync.ui.theme.WePlayTealDark
import com.weplay.sync.ui.theme.WePlayTealGlow
import com.weplay.sync.ui.theme.WePlayTealPrimary
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.cos
import kotlin.math.sin

// =========================================================================
// 1. PLANETARY ORB SOUNDSTAGE (Home Screen #2)
// Concentric planetary orbital rings, molten incandescent central acoustic orb,
// hovering 3D headphone/speaker nodes, and twinkling ambient cosmic particles.
// =========================================================================

@Composable
fun PlanetaryOrbSoundstage(
    modifier: Modifier = Modifier,
    size: Dp = 320.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "planetary")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )
    val photonProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "photon"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.5f

            // 1. Deep Space Acoustic Vignette & Volumetric Core Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayTealPrimary.copy(alpha = 0.22f),
                        WePlayTealDark.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.55f
                )
            )

            // 2. Floating Acoustic Micro-Particles (Ambient Constellation)
            val particleOffsets = listOf(
                Pair(0.18f, 0.22f), Pair(0.82f, 0.25f), Pair(0.12f, 0.72f), Pair(0.86f, 0.76f),
                Pair(0.35f, 0.12f), Pair(0.68f, 0.15f), Pair(0.25f, 0.85f), Pair(0.72f, 0.88f),
                Pair(0.08f, 0.45f), Pair(0.92f, 0.48f), Pair(0.48f, 0.08f), Pair(0.52f, 0.92f)
            )
            particleOffsets.forEachIndexed { i, (px, py) ->
                val particlePhase = Math.sin((orbitRotation * 0.05 + i * 1.3)).toFloat()
                val alpha = (0.25f + 0.55f * kotlin.math.abs(particlePhase)).coerceIn(0.1f, 0.85f)
                drawCircle(
                    color = WePlayTealGlow.copy(alpha = alpha),
                    radius = (1.2f + 0.8f * kotlin.math.abs(particlePhase)).dp.toPx(),
                    center = Offset(w * px, h * py)
                )
            }

            // 3. Concentric Precision Orbital Rings (Tilted Perspective Ellipses)
            val ringRadii = listOf(
                Pair(w * 0.28f, h * 0.14f),
                Pair(w * 0.38f, h * 0.19f),
                Pair(w * 0.46f, h * 0.23f)
            )

            rotate(degrees = -16f, pivot = Offset(cx, cy)) {
                ringRadii.forEachIndexed { idx, (rx, ry) ->
                    // Orbital track with subtle laser-etched dashes
                    drawOval(
                        color = Color(0xFF263548).copy(alpha = if (idx == 1) 0.55f else 0.35f),
                        topLeft = Offset(cx - rx, cy - ry),
                        size = Size(rx * 2f, ry * 2f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // 4. Orbiting Hardware Satellite Pods
                // Node 1: Over-Ear Studio Headphones (Outer Ring)
                val a1 = Math.toRadians((orbitRotation * 0.55f + 200.0).toDouble())
                val n1x = (cx + ringRadii[2].first * cos(a1)).toFloat()
                val n1y = (cy + ringRadii[2].second * sin(a1)).toFloat()

                // Node 2: Studio Acoustic Monitor (Inner Ring)
                val a2 = Math.toRadians((-orbitRotation * 0.75f + 35.0).toDouble())
                val n2x = (cx + ringRadii[0].first * cos(a2)).toFloat()
                val n2y = (cy + ringRadii[0].second * sin(a2)).toFloat()

                // Node 3: Auracast Mobile Receiver (Mid Ring)
                val a3 = Math.toRadians((orbitRotation * 0.45f + 315.0).toDouble())
                val n3x = (cx + ringRadii[1].first * cos(a3)).toFloat()
                val n3y = (cy + ringRadii[1].second * sin(a3)).toFloat()

                listOf(
                    Triple(n1x, n1y, "headphones"),
                    Triple(n2x, n2y, "speaker"),
                    Triple(n3x, n3y, "auracast")
                ).forEach { (nx, ny, type) ->
                    // Optical laser tether to center with traveling photon packet
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                WePlayTealGlow.copy(alpha = 0.4f),
                                Color(0xFF107A6D).copy(alpha = 0.15f)
                            ),
                            start = Offset(cx, cy),
                            end = Offset(nx, ny)
                        ),
                        start = Offset(cx, cy),
                        end = Offset(nx, ny),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Traveling photon packet along tether
                    val photonX = cx + (nx - cx) * photonProgress
                    val photonY = cy + (ny - cy) * photonProgress
                    drawCircle(
                        color = Color.White,
                        radius = 1.8.dp.toPx(),
                        center = Offset(photonX, photonY)
                    )

                    // Hardware Pod Chassis (Milled Gunmetal + Glowing Cyan Ring)
                    drawCircle(
                        color = WePlayTealPrimary.copy(alpha = 0.35f),
                        radius = 18.dp.toPx(),
                        center = Offset(nx, ny)
                    )
                    drawCircle(
                        color = Color(0xFF0D121B),
                        radius = 14.dp.toPx(),
                        center = Offset(nx, ny)
                    )
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(WePlayTealGlow, Color(0xFF107A6D), WePlayTealPrimary)
                        ),
                        radius = 14.dp.toPx(),
                        center = Offset(nx, ny),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // Crisp Vector Glyphs inside Hardware Pod
                    when (type) {
                        "headphones" -> {
                            // Headband arc
                            drawArc(
                                color = Color.White,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(nx - 5.5.dp.toPx(), ny - 6.dp.toPx()),
                                size = Size(11.dp.toPx(), 8.5.dp.toPx()),
                                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Earcups
                            drawRoundRect(
                                color = WePlayTealGlow,
                                topLeft = Offset(nx - 7.dp.toPx(), ny - 1.5.dp.toPx()),
                                size = Size(2.8.dp.toPx(), 5.5.dp.toPx()),
                                cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
                            )
                            drawRoundRect(
                                color = WePlayTealGlow,
                                topLeft = Offset(nx + 4.2.dp.toPx(), ny - 1.5.dp.toPx()),
                                size = Size(2.8.dp.toPx(), 5.5.dp.toPx()),
                                cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
                            )
                        }
                        "speaker" -> {
                            // Speaker cone
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(nx - 4.5.dp.toPx(), ny - 5.5.dp.toPx()),
                                size = Size(9.dp.toPx(), 11.dp.toPx()),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                            drawCircle(
                                color = WePlayTealGlow,
                                radius = 2.4.dp.toPx(),
                                center = Offset(nx, ny + 1.2.dp.toPx())
                            )
                        }
                        else -> {
                            // Auracast RF Wave Broadcast Waves
                            drawArc(
                                color = Color.White,
                                startAngle = -45f,
                                sweepAngle = 90f,
                                useCenter = false,
                                topLeft = Offset(nx - 5.dp.toPx(), ny - 5.dp.toPx()),
                                size = Size(10.dp.toPx(), 10.dp.toPx()),
                                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawCircle(
                                color = WePlayTealGlow,
                                radius = 2.dp.toPx(),
                                center = Offset(nx, ny)
                            )
                        }
                    }
                }
            }

            // 5. Central Hypnotic Acoustic Sphere & Wavefront Emitting Horizon
            val orbRadius = (w * 0.155f) * pulseScale

            // Radiating harmonic wavefront rings
            val waveRadii = listOf(orbRadius * 1.35f, orbRadius * 1.75f, orbRadius * 2.2f)
            waveRadii.forEachIndexed { i, r ->
                val waveAlpha = (0.35f - i * 0.10f).coerceAtLeast(0.06f)
                drawCircle(
                    color = WePlayTealGlow.copy(alpha = waveAlpha),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Radiant Corona Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayTealGlow.copy(alpha = 0.55f),
                        WePlayTealPrimary.copy(alpha = 0.30f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = orbRadius * 2.0f
                )
            )

            // Volumetric Sphere Surface (Specular 3D Lighting)
            val highlightCenter = Offset(cx - orbRadius * 0.28f, cy - orbRadius * 0.32f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),           // Crisp Specular Glint
                        Color(0xFFE8FFFB),           // Crystalline Ice-Teal
                        WePlayTealGlow,              // Luminous Cyan-Teal Peak
                        WePlayTealPrimary,           // Signature Electric Teal (#21CDB8)
                        WePlayTealDark,              // Metallic Precision Shadow
                        Color(0xFF051D1A)            // Deep Obsidian Rim
                    ),
                    center = highlightCenter,
                    radius = orbRadius
                ),
                radius = orbRadius,
                center = Offset(cx, cy)
            )

            // Centered 5-Bar WePlay Equalizer Soundmark inside the Core
            val barW = 2.dp.toPx()
            val barHBase = orbRadius * 0.40f
            val barOffsets = listOf(-2f, -1f, 0f, 1f, 2f)
            val barHeights = listOf(0.45f, 0.75f, 1.0f, 0.75f, 0.45f)

            barOffsets.forEachIndexed { idx, xOffset ->
                val hFactor = barHeights[idx]
                val bH = barHBase * hFactor
                val bx = cx + (xOffset * 5.dp.toPx())
                drawLine(
                    color = Color(0xFF091416),
                    start = Offset(bx, cy - bH * 0.5f),
                    end = Offset(bx, cy + bH * 0.5f),
                    strokeWidth = barW,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// =========================================================================
// 2. ISOMETRIC SPECTRUM CUBE (Active Broadcast Screen #3)
// 3D isometric glass cube with glowing amber frequency spectrum bars inside
// and electric blue edge reflections on a reflective dark pedestal.
// =========================================================================

@Composable
fun IsometricSpectrumCube(
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spectrum")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.52f

            // Isometric Unit Vectors
            // 30-degree isometric projection:
            val isoScale = w * 0.42f
            val cos30 = 0.866025f
            val sin30 = 0.5f

            val dx = isoScale * cos30
            val dy = isoScale * sin30
            val cubeH = isoScale * 0.75f

            // Bottom base vertices
            val bBottom = Offset(cx, cy + dy)
            val bLeft = Offset(cx - dx, cy)
            val bRight = Offset(cx + dx, cy)
            val bTop = Offset(cx, cy - dy)

            // Top lid vertices (shifted up by cubeH)
            val tBottom = Offset(cx, cy + dy - cubeH)
            val tLeft = Offset(cx - dx, cy - cubeH)
            val tRight = Offset(cx + dx, cy - cubeH)
            val tTop = Offset(cx, cy - dy - cubeH)

            // 1. Dark Reflective Pedestal / Shadow Ground
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayOrangePrimary.copy(alpha = 0.28f),
                        WePlayElectricBlue.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy + dy * 0.6f),
                    radius = dx * 1.3f
                ),
                topLeft = Offset(cx - dx * 1.3f, cy - dy),
                size = Size(dx * 2.6f, dy * 3.2f)
            )

            // 2. Back Glass Walls (Inside Cube)
            // Left-Back Wall (bTop -> tTop -> tLeft -> bLeft)
            val backLeftPath = Path().apply {
                moveTo(bTop.x, bTop.y)
                lineTo(tTop.x, tTop.y)
                lineTo(tLeft.x, tLeft.y)
                lineTo(bLeft.x, bLeft.y)
                close()
            }
            drawPath(
                path = backLeftPath,
                color = Color(0xFF0F172A).copy(alpha = 0.45f)
            )

            // Right-Back Wall (bTop -> tTop -> tRight -> bRight)
            val backRightPath = Path().apply {
                moveTo(bTop.x, bTop.y)
                lineTo(tTop.x, tTop.y)
                lineTo(tRight.x, tRight.y)
                lineTo(bRight.x, bRight.y)
                close()
            }
            drawPath(
                path = backRightPath,
                color = Color(0xFF0F172A).copy(alpha = 0.35f)
            )

            // 3. Glowing Equalizer Spectrum Bars (Inside Cube)
            // Staggered 5x5 grid of glowing amber/orange vertical spectrum bars
            val gridCols = 7
            val gridRows = 5
            for (col in 0 until gridCols) {
                for (row in 0 until gridRows) {
                    val u = (col - (gridCols - 1) * 0.5f) / (gridCols - 1)
                    val v = (row - (gridRows - 1) * 0.5f) / (gridRows - 1)

                    // Ground position in isometric plane
                    val bx = cx + (u * dx * 0.72f) + (v * -dx * 0.72f) * 0.6f
                    val by = cy + (u * dy * 0.72f) + (v * dy * 0.72f) * 0.6f

                    // Animated frequency bar height
                    val distFromCenter = kotlin.math.sqrt(u * u + v * v)
                    val wave = sin(phase + col * 0.8f + row * 0.5f) * 0.5f + 0.5f
                    val barH = (cubeH * 0.78f) * (0.35f + 0.65f * wave) * (1f - distFromCenter * 0.4f).coerceAtLeast(0.2f)

                    val barWidth = 3.5.dp.toPx()
                    val barBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8FFFB),       // Luminous White-Cyan Peak
                            WePlayTealGlow,          // Radiant Cyan (#3DE8D2)
                            WePlayTealPrimary,       // Electric Studio Teal (#21CDB8)
                            WePlayTealDark           // Deep Milled Base
                        ),
                        startY = by - barH,
                        endY = by
                    )

                    // Bar glow
                    drawLine(
                        color = WePlayTealGlow.copy(alpha = 0.38f),
                        start = Offset(bx, by),
                        end = Offset(bx, by - barH),
                        strokeWidth = barWidth * 2.2f,
                        cap = StrokeCap.Round
                    )

                    // Core Bar
                    drawLine(
                        brush = barBrush,
                        start = Offset(bx, by),
                        end = Offset(bx, by - barH),
                        strokeWidth = barWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 4. Front Glass Facets (Translucent with Blue/Cyan Edge Rims)
            // Left Front Facet (bLeft -> tLeft -> tBottom -> bBottom)
            val frontLeftPath = Path().apply {
                moveTo(bLeft.x, bLeft.y)
                lineTo(tLeft.x, tLeft.y)
                lineTo(tBottom.x, tBottom.y)
                lineTo(bBottom.x, bBottom.y)
                close()
            }
            drawPath(
                path = frontLeftPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        WePlayElectricBlue.copy(alpha = 0.08f),
                        Color.Transparent,
                        WePlayElectricCyan.copy(alpha = 0.04f)
                    ),
                    start = bLeft,
                    end = tBottom
                )
            )

            // Right Front Facet (bRight -> tRight -> tBottom -> bBottom)
            val frontRightPath = Path().apply {
                moveTo(bRight.x, bRight.y)
                lineTo(tRight.x, tRight.y)
                lineTo(tBottom.x, tBottom.y)
                lineTo(bBottom.x, bBottom.y)
                close()
            }
            drawPath(
                path = frontRightPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        WePlayElectricCyan.copy(alpha = 0.12f),
                        Color.Transparent,
                        WePlayElectricBlue.copy(alpha = 0.05f)
                    ),
                    start = bRight,
                    end = tBottom
                )
            )

            // Top Glass Lid (tBottom -> tLeft -> tTop -> tRight)
            val topLidPath = Path().apply {
                moveTo(tBottom.x, tBottom.y)
                lineTo(tLeft.x, tLeft.y)
                lineTo(tTop.x, tTop.y)
                lineTo(tRight.x, tRight.y)
                close()
            }
            drawPath(
                path = topLidPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        WePlayElectricCyan.copy(alpha = 0.16f),
                        Color.Transparent,
                        Color(0xFF1E293B).copy(alpha = 0.25f)
                    ),
                    start = tTop,
                    end = tBottom
                )
            )

            // 5. Crisp Isometric Edges & Chamfer Wireframe
            val edgeColor = WePlayElectricCyan.copy(alpha = 0.75f)
            val edgeStroke = 1.4.dp.toPx()

            // Vertical front pillar
            drawLine(edgeColor, bBottom, tBottom, edgeStroke)
            drawLine(edgeColor.copy(alpha = 0.45f), bLeft, tLeft, edgeStroke)
            drawLine(edgeColor.copy(alpha = 0.45f), bRight, tRight, edgeStroke)

            // Top lid rim
            drawPath(topLidPath, edgeColor, style = Stroke(width = edgeStroke))

            // Bottom rim
            val bottomRim = Path().apply {
                moveTo(bLeft.x, bLeft.y)
                lineTo(bBottom.x, bBottom.y)
                lineTo(bRight.x, bRight.y)
            }
            drawPath(bottomRim, edgeColor.copy(alpha = 0.5f), style = Stroke(width = edgeStroke))
        }
    }
}

// =========================================================================
// 3. VINYL ALBUM COVER (Now Playing Screen #6)
// Square cosmic landscape album cover with a grooved black vinyl record
// physically sliding out from the sleeve on the right.
// =========================================================================

@Composable
fun VinylAlbumCover(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // We compose the layout:
        // Left side holds the square sleeve (with alien planetary landscape)
        // Right side reveals the sliding vinyl record!
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // 1. Sliding Vinyl Record (Positioned rightwards, partially behind sleeve)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.CenterEnd)
                    .rotate(if (isPlaying) spinAngle else 0f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val cx = w * 0.5f
                    val cy = h * 0.5f

                    // Deep vinyl obsidian body
                    drawCircle(
                        color = Color(0xFF0C0E14),
                        radius = cx,
                        center = Offset(cx, cy)
                    )

                    // Concentric Micro-Grooves Sheen
                    val grooveRadii = listOf(0.92f, 0.85f, 0.78f, 0.71f, 0.64f, 0.57f, 0.50f)
                    grooveRadii.forEach { factor ->
                        drawCircle(
                            color = Color(0xFF1E2533).copy(alpha = 0.65f),
                            radius = cx * factor,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Specular Sheen (Vinyl light reflection cones)
                    rotate(degrees = 45f, pivot = Offset(cx, cy)) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.12f),
                            start = Offset(cx, 0f),
                            end = Offset(cx, h),
                            strokeWidth = 24.dp.toPx()
                        )
                    }

                    // Center Studio Teal Label
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE8FFFB),
                                WePlayTealGlow,
                                WePlayTealPrimary,
                                WePlayTealDark
                            ),
                            center = Offset(cx, cy),
                            radius = cx * 0.35f
                        ),
                        radius = cx * 0.35f,
                        center = Offset(cx, cy)
                    )

                    // Spindle Hole
                    drawCircle(
                        color = WePlayObsidian,
                        radius = cx * 0.08f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        radius = cx * 0.08f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // 2. Cosmic Alien Landscape Album Sleeve (Square Card on Left)
            Box(
                modifier = Modifier
                    .size(235.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.2.dp, WePlayBorder, RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height

                    // Twilight Cosmic Sky Gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF070B12),       // Deep Space Obsidian
                                Color(0xFF0F1A28),       // Twilight Titanium
                                Color(0xFF14303B),       // Cyan Nebula Horizon
                                Color(0xFF0A131C)        // Deep Studio Floor Shadow
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Massive Luminous Acoustic Celestial Sphere (Electric Studio Teal)
                    val planetCenter = Offset(w * 0.45f, h * 0.36f)
                    val planetRadius = w * 0.30f

                    // Atmospheric Corona
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                WePlayTealGlow.copy(alpha = 0.45f),
                                WePlayTealPrimary.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = planetCenter,
                            radius = planetRadius * 1.5f
                        )
                    )

                    // Sphere Body
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),       // Specular glint
                                Color(0xFFE8FFFB),       // Luminous ice teal
                                WePlayTealGlow,          // Cyan corona
                                WePlayTealPrimary,       // Electric studio teal
                                WePlayTealDark,          // Deep shadow
                                Color(0xFF041916)        // Edge
                            ),
                            center = Offset(planetCenter.x - planetRadius * 0.3f, planetCenter.y - planetRadius * 0.3f),
                            radius = planetRadius
                        ),
                        radius = planetRadius,
                        center = planetCenter
                    )

                    // Acoustic Wavefront Ring
                    rotate(degrees = -20f, pivot = planetCenter) {
                        drawOval(
                            brush = Brush.horizontalGradient(
                                listOf(Color.Transparent, WePlayTealGlow, Color(0xFFE8FFFB), Color.Transparent)
                            ),
                            topLeft = Offset(planetCenter.x - planetRadius * 1.5f, planetCenter.y - planetRadius * 0.28f),
                            size = Size(planetRadius * 3.0f, planetRadius * 0.56f),
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                    }

                    // Harmonic Frequency Horizon Ridges
                    val horizonPath = Path().apply {
                        moveTo(0f, h)
                        lineTo(0f, h * 0.72f)
                        cubicTo(w * 0.25f, h * 0.60f, w * 0.45f, h * 0.82f, w * 0.70f, h * 0.65f)
                        lineTo(w, h * 0.70f)
                        lineTo(w, h)
                        close()
                    }
                    drawPath(
                        path = horizonPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F1E28),
                                Color(0xFF070D14)
                            ),
                            startY = h * 0.60f,
                            endY = h
                        )
                    )

                    // Studio Precision Grid Lines on Lower Third
                    for (step in 1..4) {
                        val ly = h * 0.74f + step * 8.dp.toPx()
                        drawLine(
                            color = Color(0xFF1E3040).copy(alpha = 0.35f),
                            start = Offset(0f, ly),
                            end = Offset(w, ly),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Sleeve Subtle Edge Gloss
                    drawLine(
                        color = Color.White.copy(alpha = 0.22f),
                        start = Offset(0f, 0f),
                        end = Offset(w, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. SONAR RADAR MESH (Discover Devices Screen #8)
// Circular concentric sonar radar with central smartphone icon, rotating
// scanning beam, 3 glowing electric-blue node dots, and connecting rays.
// =========================================================================

@Composable
fun SonarRadarMesh(
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sonar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.5f

            // 1. Concentric Sonar Rings
            val ringRadii = listOf(w * 0.18f, w * 0.30f, w * 0.42f)
            ringRadii.forEachIndexed { i, r ->
                drawCircle(
                    color = WePlayElectricBlue.copy(alpha = if (i == 2) 0.25f else 0.4f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Crosshair guidelines (subtle)
            drawLine(
                color = WePlayElectricBlue.copy(alpha = 0.18f),
                start = Offset(cx, cy - w * 0.46f),
                end = Offset(cx, cy + w * 0.46f),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = WePlayElectricBlue.copy(alpha = 0.18f),
                start = Offset(cx - w * 0.46f, cy),
                end = Offset(cx + w * 0.46f, cy),
                strokeWidth = 1.dp.toPx()
            )

            // 2. Rotating Radar Sweep Beam
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        WePlayElectricCyan.copy(alpha = 0.05f),
                        WePlayElectricBlue.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy)
                ),
                startAngle = sweepAngle - 45f,
                sweepAngle = 45f,
                useCenter = true,
                topLeft = Offset(cx - ringRadii.last(), cy - ringRadii.last()),
                size = Size(ringRadii.last() * 2f, ringRadii.last() * 2f)
            )

            // 3. Three Discovered Blue Device Nodes
            val nodes = listOf(
                Pair(ringRadii[1], 35.0),    // Top right (AirPods)
                Pair(ringRadii[2], 165.0),   // Mid left (JBL Flip 6)
                Pair(ringRadii[1], 255.0)    // Bottom left (Sony XM5)
            )

            nodes.forEach { (radius, deg) ->
                val rad = Math.toRadians(deg)
                val nx = (cx + radius * cos(rad)).toFloat()
                val ny = (cy + radius * sin(rad)).toFloat()

                // Radiant Connection Ray from Phone to Node
                drawLine(
                    color = WePlayElectricCyan.copy(alpha = 0.65f),
                    start = Offset(cx, cy),
                    end = Offset(nx, ny),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Outer pulsing halo
                drawCircle(
                    color = WePlayElectricCyan.copy(alpha = 0.35f),
                    radius = 9.dp.toPx(),
                    center = Offset(nx, ny)
                )

                // Core electric-blue node dot
                drawCircle(
                    color = WePlayElectricCyan,
                    radius = 4.5.dp.toPx(),
                    center = Offset(nx, ny)
                )
            }

            // 4. Central Smartphone Silhouette
            val phoneW = 20.dp.toPx()
            val phoneH = 34.dp.toPx()
            val phoneCorner = 4.dp.toPx()

            // Dark circular phone backing
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 22.dp.toPx(),
                center = Offset(cx, cy)
            )
            drawCircle(
                color = WePlayElectricBlue,
                radius = 22.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Smartphone frame
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx - phoneW * 0.5f, cy - phoneH * 0.5f),
                size = Size(phoneW, phoneH),
                cornerRadius = CornerRadius(phoneCorner, phoneCorner),
                style = Stroke(width = 1.8.dp.toPx())
            )
            // Screen notch / speaker line
            drawLine(
                color = Color.White,
                start = Offset(cx - 3.dp.toPx(), cy - phoneH * 0.5f + 3.dp.toPx()),
                end = Offset(cx + 3.dp.toPx(), cy - phoneH * 0.5f + 3.dp.toPx()),
                strokeWidth = 1.2.dp.toPx()
            )
            // Home indicator pill
            drawLine(
                color = Color.White,
                start = Offset(cx - 3.dp.toPx(), cy + phoneH * 0.5f - 4.dp.toPx()),
                end = Offset(cx + 3.dp.toPx(), cy + phoneH * 0.5f - 4.dp.toPx()),
                strokeWidth = 1.2.dp.toPx()
            )
        }
    }
}

// =========================================================================
// 5. PENTHOUSE SUNSET ORB (Onboarding Screen #10)
// Architectural penthouse sunset scene with floating glowing acoustic sphere
// radiating circular wavefronts into the room.
// =========================================================================

@Composable
fun PenthouseSunsetOrb(
    modifier: Modifier = Modifier,
    height: Dp = 380.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "penthouse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Architectural Studio Atmosphere (Deep Obsidian to Horizon Gradient)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06090E),       // Pure Void Space
                        Color(0xFF0C131D),       // Milled Charcoal Titanium
                        Color(0xFF13222E),       // Acoustic Studio Horizon
                        Color(0xFF09141A),       // Floor Specular Basin
                        Color(0xFF05080D)        // Foreground Chassis
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // 2. Vertical Acoustic Diffuser Baffle Fins (Studio Architecture)
            val finCount = 18
            val finSpacing = w / finCount
            for (i in 0 until finCount) {
                val finX = i * finSpacing
                val distCenter = kotlin.math.abs(finX - w * 0.5f) / (w * 0.5f)
                val finH = h * 0.55f * (0.5f + 0.5f * distCenter)
                val finAlpha = 0.15f + 0.12f * (1f - distCenter)

                drawLine(
                    color = Color(0xFF1E2D3E).copy(alpha = finAlpha),
                    start = Offset(finX, 0f),
                    end = Offset(finX, finH),
                    strokeWidth = 1.5.dp.toPx()
                )
                // Subtle illuminated chamfer base
                drawCircle(
                    color = WePlayTealGlow.copy(alpha = 0.25f * (1f - distCenter)),
                    radius = 1.dp.toPx(),
                    center = Offset(finX, finH)
                )
            }

            // 3. Precision Perspective Floor Grid (Studio Ground Stage)
            val floorTop = h * 0.58f
            for (step in 1..6) {
                val lineY = floorTop + (h - floorTop) * (step / 6f) * (step / 6f)
                val lineAlpha = 0.12f + 0.15f * (step / 6f)
                drawLine(
                    color = Color(0xFF1C2C3D).copy(alpha = lineAlpha),
                    start = Offset(0f, lineY),
                    end = Offset(w, lineY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 4. Central Monolithic Floating Acoustic Resonator Orb
            val orbCenter = Offset(w * 0.5f, h * 0.40f)
            val baseRadius = 32.dp.toPx() * pulse

            // Radiating Concentric Wavefront Rings (Harmonic Audio Expansion)
            val waveRadii = listOf(baseRadius * 1.6f, baseRadius * 2.5f, baseRadius * 3.6f, baseRadius * 4.8f)
            waveRadii.forEachIndexed { i, r ->
                val waveAlpha = (0.42f - i * 0.09f).coerceAtLeast(0.06f)
                drawCircle(
                    color = WePlayTealGlow.copy(alpha = waveAlpha),
                    radius = r,
                    center = orbCenter,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Volumetric Ambient Floor Specular Pool
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayTealPrimary.copy(alpha = 0.40f),
                        WePlayTealDark.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(orbCenter.x, floorTop + 24.dp.toPx()),
                    radius = w * 0.42f
                ),
                topLeft = Offset(orbCenter.x - w * 0.42f, floorTop),
                size = Size(w * 0.84f, 48.dp.toPx())
            )

            // Core Acoustic Resonator Sphere (Signature #21CDB8 Electric Studio Teal)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),           // Crisp Specular Glint
                        Color(0xFFE8FFFB),           // Crystalline Ice-Teal
                        WePlayTealGlow,              // Peak Corona Glow (#3DE8D2)
                        WePlayTealPrimary,           // Electric Studio Teal (#21CDB8)
                        WePlayTealDark,              // Metallic Shadow
                        Color(0xFF041815)            // Obsidian Rim
                    ),
                    center = Offset(orbCenter.x - baseRadius * 0.28f, orbCenter.y - baseRadius * 0.32f),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = orbCenter
            )

            // Equator Precision Filament Ring
            drawOval(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(orbCenter.x - baseRadius * 1.2f, orbCenter.y - baseRadius * 0.25f),
                size = Size(baseRadius * 2.4f, baseRadius * 0.5f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

// =========================================================================
// 6. FLOWING COPPER RIBBON (Splash #1 & Join #4)
// Metallic silky copper filament ribbon wave sweeping across canvas bottom.
// =========================================================================

@Composable
fun FlowingCopperRibbon(
    modifier: Modifier = Modifier,
    height: Dp = 140.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = this.size.width
        val h = this.size.height

        // Ribbon Layer 1: Ambient teal haze
        val hazePath = Path().apply {
            moveTo(0f, h * 0.65f)
            cubicTo(w * 0.35f, h * 0.15f, w * 0.65f, h * 0.95f, w, h * 0.45f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = hazePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    WePlayTealPrimary.copy(alpha = 0.22f),
                    WePlayTealDark.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = h
            )
        )

        // Ribbon Layer 2: Main metallic electric-teal ribbon spline
        val ribbonPath = Path().apply {
            moveTo(0f, h * 0.75f)
            cubicTo(w * 0.28f, h * 0.32f, w * 0.68f, h * 0.88f, w, h * 0.52f)
        }
        drawPath(
            path = ribbonPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0C4D44),
                    Color(0xFF168375),
                    WePlayTealPrimary,
                    WePlayTealGlow,
                    Color(0xFF1AA392)
                )
            ),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Ribbon Layer 3: Secondary parallel cyan filament
        val ribbonPath2 = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.32f, h * 0.42f, w * 0.72f, h * 0.94f, w, h * 0.60f)
        }
        drawPath(
            path = ribbonPath2,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF08332D),
                    Color(0xFF1AA392),
                    Color(0xFFE0FFF9),
                    WePlayTealPrimary,
                    Color(0xFF0C5248)
                )
            ),
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// =========================================================================
// 7. WEPLAY BOTTOM NAV BAR (Shared across Home, Library, Rooms, Settings)
// Docked dark glass bar with 4 tabs and glowing orange active tab pill.
// =========================================================================

@Composable
fun WePlayBottomNav(
    currentTab: AppScreen,
    onTabSelect: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF090C12).copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = WePlayBorderSubtle,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Pair(AppScreen.SHARE_HOME, "Home"),
                Pair(AppScreen.LIBRARY, "Music"),
                Pair(AppScreen.MULTI_OUTPUT_ROOM, "Devices"),
                Pair(AppScreen.SETTINGS, "Settings")
            )

            tabs.forEach { (screen, label) ->
                val isActive = currentTab == screen
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelect(screen) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val tintColor = if (isActive) WePlayOrangePrimary else WePlayTextSecondary
                    when (screen) {
                        AppScreen.SHARE_HOME -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = label,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                        AppScreen.LIBRARY -> WePlayIcon(
                            type = WePlayIconType.LIBRARY,
                            tint = tintColor,
                            size = 22.dp
                        )
                        AppScreen.MULTI_OUTPUT_ROOM -> WePlayIcon(
                            type = WePlayIconType.ROOM,
                            tint = tintColor,
                            size = 22.dp
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = label,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isActive) WePlayOrangePrimary else WePlayTextSecondary
                    )
                }
            }
        }
    }
}
