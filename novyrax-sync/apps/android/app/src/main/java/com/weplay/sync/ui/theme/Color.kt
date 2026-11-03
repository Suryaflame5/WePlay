package com.weplay.sync.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// WEPLAY MASTER CINEMATIC DESIGN SYSTEM — PRIMARY VISUAL TOKENS
// =========================================================================

// Deep Cosmic Ground & Glass Foundations
val WePlayObsidian = Color(0xFF06080C)          // Deepest Space Obsidian Canvas
val WePlaySurface = Color(0xFF0E121A)           // Frosted Glass Chassis Card
val WePlaySurfaceElevated = Color(0xFF141A26)   // Recessed Pod / Input Surface
val WePlayBorder = Color(0xFF1E2638)            // Glass Chamfer Edge Border
val WePlayBorderSubtle = Color(0xFF161E2E)      // Subtle Structural Grid Line

// Radiant Molten Solar Accent System
val WePlayOrangePrimary = Color(0xFFFF5500)     // Radiant Molten Core Action
val WePlayOrangeGlow = Color(0xFFFF7A00)        // Warm Amber Corona Glow
val WePlayOrangeDark = Color(0xFFCC4400)        // Deep Molten Shadow
val WePlayOrangeSubtle = Color(0x28FF5500)      // Diffuse Radial Ambient Glow

// Electric Connectivity Accent System
val WePlayElectricBlue = Color(0xFF2E7CF6)      // Sonar Radar / Network Node
val WePlayElectricCyan = Color(0xFF38B6FF)      // PTP Phase Lock / Ray
val WePlayElectricBlueSubtle = Color(0x282E7CF6)

// Hi-Fi Telemetry & Signal Enamel
val WePlayStatusGreen = Color(0xFF00E676)       // Connected / dB Level Meter
val WePlayStatusRed = Color(0xFFFF334B)         // LIVE Broadcast / Critical Stop
val WePlayStatusAmber = Color(0xFFFFB300)       // Hi-Res Audio / Buffer Depth

// High-Contrast Typographic Enamel
val WePlayTextPrimary = Color(0xFFFFFFFF)       // Crisp White Milled Typography
val WePlayTextSecondary = Color(0xFF8B95A5)     // Subdued Studio Label
val WePlayTextMuted = Color(0xFF5A6475)         // Technical Telemetry Annotations

// Backward Compatibility Aliases for Atelier / Instrumentarium references
val AtelierObsidian = WePlayObsidian
val AtelierGunmetal = WePlaySurface
val AtelierTray = WePlaySurfaceElevated
val AtelierChamfer = WePlayBorder
val AtelierTextPrimary = WePlayTextPrimary
val AtelierTextSecondary = WePlayTextSecondary
val SignalVermilion = WePlayOrangePrimary
val SignalVermilionSubtle = WePlayOrangeSubtle
val ElectricCobalt = WePlayElectricBlue
val ElectricCobaltSubtle = WePlayElectricBlueSubtle
val CalibratedGold = WePlayStatusAmber
val CalibratedGoldSubtle = Color(0x24FFB300)
val PrecisionCyan = WePlayStatusGreen
val PrecisionCyanSubtle = Color(0x2400E676)
val HardwareBarred = WePlayStatusRed

// Light Palette (Technical Studio Drafting Mode)
val CanvasLight = Color(0xFFF0EDE5)
val SurfaceLight = Color(0xFFFAF8F5)
val SurfaceSubtleLight = Color(0xFFE5E1D8)
val TextPrimaryLight = Color(0xFF0D1117)
val TextSecondaryLight = Color(0xFF6B7280)
val SeparatorLight = Color(0xFFD1CBC0)

// Dark Palette (Cinematic Mode)
val CanvasDark = WePlayObsidian
val SurfaceDark = WePlaySurface
val SurfaceSubtleDark = WePlaySurfaceElevated
val TextPrimaryDark = WePlayTextPrimary
val TextSecondaryDark = WePlayTextSecondary
val SeparatorDark = WePlayBorder

data class WePlayColorPalette(
    val canvas: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val separator: Color,
    val accent: Color,
    val accentSubtle: Color,
    val signalVermilion: Color,
    val electricCobalt: Color,
    val calibratedGold: Color,
    val precisionCyan: Color,
    val coral: Color,
    val blue: Color,
    val lavender: Color,
    val gold: Color,
    val warning: Color,
    val error: Color,
    val success: Color,
    val isDark: Boolean
)

val LightPalette = WePlayColorPalette(
    canvas = CanvasLight,
    surface = SurfaceLight,
    surfaceSubtle = SurfaceSubtleLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    separator = SeparatorLight,
    accent = WePlayOrangePrimary,
    accentSubtle = WePlayOrangeSubtle,
    signalVermilion = WePlayOrangePrimary,
    electricCobalt = WePlayElectricBlue,
    calibratedGold = WePlayStatusAmber,
    precisionCyan = WePlayStatusGreen,
    coral = WePlayOrangePrimary,
    blue = WePlayElectricBlue,
    lavender = Color(0xFF8B7CF6),
    gold = WePlayStatusAmber,
    warning = Color(0xFFF59E0B),
    error = WePlayStatusRed,
    success = WePlayStatusGreen,
    isDark = false
)

val DarkPalette = WePlayColorPalette(
    canvas = CanvasDark,
    surface = SurfaceDark,
    surfaceSubtle = SurfaceSubtleDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    separator = SeparatorDark,
    accent = WePlayOrangePrimary,
    accentSubtle = WePlayOrangeSubtle,
    signalVermilion = WePlayOrangePrimary,
    electricCobalt = WePlayElectricBlue,
    calibratedGold = WePlayStatusAmber,
    precisionCyan = WePlayStatusGreen,
    coral = WePlayOrangePrimary,
    blue = WePlayElectricBlue,
    lavender = Color(0xFF8B7CF6),
    gold = WePlayStatusAmber,
    warning = Color(0xFFF59E0B),
    error = WePlayStatusRed,
    success = WePlayStatusGreen,
    isDark = true
)
