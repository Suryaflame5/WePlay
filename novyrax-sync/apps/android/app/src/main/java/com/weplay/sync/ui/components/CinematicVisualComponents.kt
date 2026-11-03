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
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(38000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.5f

            // 1. Cosmic Background Nebula Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayOrangePrimary.copy(alpha = 0.16f),
                        WePlayOrangeDark.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.52f
                )
            )

            // 2. Concentric Planetary Orbital Rings (Tilted Ellipses)
            val ringRadii = listOf(
                Pair(w * 0.28f, h * 0.14f),
                Pair(w * 0.38f, h * 0.19f),
                Pair(w * 0.46f, h * 0.23f)
            )

            rotate(degrees = -18f, pivot = Offset(cx, cy)) {
                ringRadii.forEachIndexed { idx, (rx, ry) ->
                    val ringAlpha = if (idx == 1) 0.38f else 0.22f
                    drawOval(
                        color = Color(0xFF6B7280).copy(alpha = ringAlpha),
                        topLeft = Offset(cx - rx, cy - ry),
                        size = Size(rx * 2f, ry * 2f),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }

                // 3. Orbiting Nodes on Rings
                // Node 1: Left Headphones Node on ring 2
                val a1 = Math.toRadians((orbitRotation * 0.6f + 210.0).toDouble())
                val n1x = (cx + ringRadii[1].first * cos(a1)).toFloat()
                val n1y = (cy + ringRadii[1].second * sin(a1)).toFloat()

                // Node 2: Speaker node on ring 0
                val a2 = Math.toRadians((-orbitRotation * 0.8f + 40.0).toDouble())
                val n2x = (cx + ringRadii[0].first * cos(a2)).toFloat()
                val n2y = (cy + ringRadii[0].second * sin(a2)).toFloat()

                // Node 3: Studio Earbud node on ring 2
                val a3 = Math.toRadians((orbitRotation * 0.5f + 320.0).toDouble())
                val n3x = (cx + ringRadii[2].first * cos(a3)).toFloat()
                val n3y = (cy + ringRadii[2].second * sin(a3)).toFloat()

                // Draw Nodes with Glass Halo
                listOf(
                    Triple(n1x, n1y, "headphones"),
                    Triple(n2x, n2y, "speaker"),
                    Triple(n3x, n3y, "node")
                ).forEach { (nx, ny, type) ->
                    // Subtle tether line to center
                    drawLine(
                        color = WePlayOrangeGlow.copy(alpha = 0.15f),
                        start = Offset(cx, cy),
                        end = Offset(nx, ny),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Node outer glow
                    drawCircle(
                        color = WePlayOrangePrimary.copy(alpha = 0.25f),
                        radius = 16.dp.toPx(),
                        center = Offset(nx, ny)
                    )
                    // Node dark glass pod
                    drawCircle(
                        color = Color(0xFF141A26),
                        radius = 12.dp.toPx(),
                        center = Offset(nx, ny)
                    )
                    drawCircle(
                        color = WePlayOrangeGlow,
                        radius = 12.dp.toPx(),
                        center = Offset(nx, ny),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // Node center glyph (drawn procedurally)
                    when (type) {
                        "headphones" -> {
                            drawArc(
                                color = Color.White,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(nx - 5.dp.toPx(), ny - 5.dp.toPx()),
                                size = Size(10.dp.toPx(), 8.dp.toPx()),
                                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(nx - 6.dp.toPx(), ny - 1.dp.toPx()),
                                size = Size(2.5.dp.toPx(), 5.dp.toPx()),
                                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                            )
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(nx + 3.5.dp.toPx(), ny - 1.dp.toPx()),
                                size = Size(2.5.dp.toPx(), 5.dp.toPx()),
                                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                            )
                        }
                        "speaker" -> {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(nx - 4.dp.toPx(), ny - 5.dp.toPx()),
                                size = Size(8.dp.toPx(), 10.dp.toPx()),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(nx, ny + 1.dp.toPx())
                            )
                        }
                        else -> {
                            drawCircle(
                                color = WePlayOrangeGlow,
                                radius = 3.dp.toPx(),
                                center = Offset(nx, ny)
                            )
                        }
                    }
                }
            }

            // 4. Molten Incandescent Acoustic Orb (Centerpiece)
            val orbRadius = (w * 0.155f) * pulseScale

            // Outer radiant corona glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayOrangePrimary.copy(alpha = 0.55f),
                        WePlayOrangeDark.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = orbRadius * 2.2f
                )
            )

            // Volumetric Sphere Surface Gradients
            // Highlight offset towards upper-left
            val highlightCenter = Offset(cx - orbRadius * 0.28f, cy - orbRadius * 0.32f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF2D0),           // Solar White Incandescence
                        Color(0xFFFFB300),           // Radiant Gold-Amber
                        WePlayOrangePrimary,         // Molten Orange Core
                        Color(0xFFC43300),           // Deep Magma Edge
                        Color(0xFF380800)            // Shadow Rim
                    ),
                    center = highlightCenter,
                    radius = orbRadius
                ),
                radius = orbRadius,
                center = Offset(cx, cy)
            )

            // Inner molten texture filament ring
            drawCircle(
                color = Color.White.copy(alpha = 0.45f),
                radius = orbRadius * 0.35f,
                center = highlightCenter,
                style = Stroke(width = 1.dp.toPx())
            )
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
                            Color(0xFFFFB300),       // Bright Amber Top
                            WePlayOrangePrimary,     // Molten Orange
                            WePlayOrangeDark         // Base
                        ),
                        startY = by - barH,
                        endY = by
                    )

                    // Bar glow
                    drawLine(
                        color = WePlayOrangeGlow.copy(alpha = 0.35f),
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

                    // Center Amber Label
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFB300),
                                WePlayOrangePrimary,
                                WePlayOrangeDark
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
                                Color(0xFF16082B),       // Deep Space Violet
                                Color(0xFF4A154B),       // Sunset Nebula Magenta
                                Color(0xFF8B2535),       // Warm Rose Horizon
                                Color(0xFF261014)        // Valley Shadow
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Massive Glowing Alien Moon / Planet
                    val planetCenter = Offset(w * 0.42f, h * 0.32f)
                    val planetRadius = w * 0.34f

                    // Planet corona
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF7A00).copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            center = planetCenter,
                            radius = planetRadius * 1.35f
                        )
                    )

                    // Planet body
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFC078),
                                Color(0xFFFF6A3D),
                                Color(0xFF531238),
                                Color(0xFF1E0824)
                            ),
                            center = Offset(planetCenter.x - planetRadius * 0.3f, planetCenter.y - planetRadius * 0.2f),
                            radius = planetRadius
                        ),
                        radius = planetRadius,
                        center = planetCenter
                    )

                    // Planet Ring
                    rotate(degrees = -25f, pivot = planetCenter) {
                        drawOval(
                            color = Color(0xFFFFC078).copy(alpha = 0.65f),
                            topLeft = Offset(planetCenter.x - planetRadius * 1.4f, planetCenter.y - planetRadius * 0.25f),
                            size = Size(planetRadius * 2.8f, planetRadius * 0.5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Jagged Craggy Mountain Ridge Silhouettes
                    val mountainPath = Path().apply {
                        moveTo(0f, h)
                        lineTo(0f, h * 0.72f)
                        lineTo(w * 0.18f, h * 0.62f)
                        lineTo(w * 0.34f, h * 0.74f)
                        lineTo(w * 0.52f, h * 0.55f)
                        lineTo(w * 0.68f, h * 0.70f)
                        lineTo(w * 0.85f, h * 0.58f)
                        lineTo(w, h * 0.68f)
                        lineTo(w, h)
                        close()
                    }
                    drawPath(
                        path = mountainPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1C0D26),
                                Color(0xFF09040D)
                            ),
                            startY = h * 0.55f,
                            endY = h
                        )
                    )

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

            // 1. Floor-to-ceiling Sunset Sky Horizon
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0B1E),       // Twilight Space
                        Color(0xFF381432),       // Violet Sunset
                        Color(0xFF9E3D22),       // Golden Twilight Horizon
                        Color(0xFFFF7A00).copy(alpha = 0.85f), // Sunset Glow
                        Color(0xFF14080A)        // Penthouse Floor Shadow
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // 2. Window Mullions & City Skyline Silhouette
            val windowW = w * 0.33f
            drawLine(
                color = Color(0xFF1E1724),
                start = Offset(windowW, 0f),
                end = Offset(windowW, h * 0.75f),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = Color(0xFF1E1724),
                start = Offset(windowW * 2f, 0f),
                end = Offset(windowW * 2f, h * 0.75f),
                strokeWidth = 3.dp.toPx()
            )

            // City Skyline Buildings (Subtle silhouettes on horizon)
            val skylineTop = h * 0.52f
            drawRect(
                color = Color(0xFF1A0A14),
                topLeft = Offset(w * 0.08f, skylineTop - 25.dp.toPx()),
                size = Size(18.dp.toPx(), 45.dp.toPx())
            )
            drawRect(
                color = Color(0xFF1A0A14),
                topLeft = Offset(w * 0.22f, skylineTop - 40.dp.toPx()),
                size = Size(26.dp.toPx(), 60.dp.toPx())
            )
            drawRect(
                color = Color(0xFF1A0A14),
                topLeft = Offset(w * 0.72f, skylineTop - 35.dp.toPx()),
                size = Size(22.dp.toPx(), 55.dp.toPx())
            )

            // 3. Penthouse Living Room Interior: Low Modern Couch & Coffee Table
            val couchY = h * 0.68f
            drawRoundRect(
                color = Color(0xFF181B24),
                topLeft = Offset(w * 0.12f, couchY),
                size = Size(w * 0.76f, 32.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // House Plant Silhouette (Right corner)
            drawCircle(
                color = Color(0xFF10141D),
                radius = 28.dp.toPx(),
                center = Offset(w * 0.88f, couchY + 10.dp.toPx())
            )

            // 4. Floating Incandescent Acoustic Orb with Radiating Circular Wavefronts
            val orbCenter = Offset(w * 0.5f, h * 0.42f)
            val baseRadius = 26.dp.toPx() * pulse

            // Radiating Concentric Wavefront Rings
            val waveRadii = listOf(baseRadius * 1.8f, baseRadius * 2.8f, baseRadius * 3.8f, baseRadius * 5.0f)
            waveRadii.forEachIndexed { i, r ->
                val waveAlpha = (0.45f - i * 0.09f).coerceAtLeast(0.08f)
                drawCircle(
                    color = WePlayOrangeGlow.copy(alpha = waveAlpha),
                    radius = r,
                    center = orbCenter,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Warm Volumetric Ambient Floor Glow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WePlayOrangePrimary.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(orbCenter.x, couchY - 10.dp.toPx()),
                    radius = w * 0.38f
                ),
                topLeft = Offset(orbCenter.x - w * 0.38f, couchY - 30.dp.toPx()),
                size = Size(w * 0.76f, 40.dp.toPx())
            )

            // Core Orb Body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF6DD),           // Hot White Core
                        Color(0xFFFFB300),           // Solar Gold
                        WePlayOrangePrimary,         // Molten Orange
                        Color(0xFFC03600),           // Deep Rim
                        Color(0xFF380800)            // Shadow
                    ),
                    center = Offset(orbCenter.x - baseRadius * 0.25f, orbCenter.y - baseRadius * 0.25f),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = orbCenter
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

        // Ribbon Layer 1: Ambient copper haze
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
                    Color(0xFFFF7A00).copy(alpha = 0.25f),
                    Color(0xFF8B3A00).copy(alpha = 0.10f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = h
            )
        )

        // Ribbon Layer 2: Main metallic ribbon spline
        val ribbonPath = Path().apply {
            moveTo(0f, h * 0.75f)
            cubicTo(w * 0.28f, h * 0.32f, w * 0.68f, h * 0.88f, w, h * 0.52f)
        }
        drawPath(
            path = ribbonPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF8B4513),
                    Color(0xFFFFB300),
                    WePlayOrangePrimary,
                    Color(0xFFFF7A00),
                    Color(0xFFB85E00)
                )
            ),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Ribbon Layer 3: Secondary parallel filament
        val ribbonPath2 = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.32f, h * 0.42f, w * 0.72f, h * 0.94f, w, h * 0.60f)
        }
        drawPath(
            path = ribbonPath2,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF5C2600),
                    Color(0xFFFF8C00),
                    Color(0xFFFFE082),
                    WePlayOrangePrimary,
                    Color(0xFF7A3200)
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
                Pair(AppScreen.LIBRARY, "Library"),
                Pair(AppScreen.MULTI_OUTPUT_ROOM, "Rooms"),
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
