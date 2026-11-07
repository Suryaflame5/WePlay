package com.weplay.sync.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// WEPLAY MASTER CLASS DESIGN SYSTEM — BRAND ICON ALIGNED COLOR TOKENS
// Primary Brand Color: #21CDB8 (WePlay Electric Teal / Studio Cyan)
// Luxury Hi-Fi Dark Architecture: Bang & Olufsen / Teenage Engineering
// =========================================================================

// Deep Obsidian Ground & Milled Metal Chassis
val WePlayObsidian = Color(0xFF080B10)          // Deep Space Obsidian Canvas
val WePlaySurface = Color(0xFF0F141C)           // Frosted Glass & Anodized Chassis
val WePlaySurfaceElevated = Color(0xFF151D28)   // Recessed Pod / Milled Tray Surface
val WePlayBorder = Color(0xFF1F2B3A)            // Precision Chamfer Edge Border
val WePlayBorderSubtle = Color(0xFF16202C)      // Structural Micro-Grid Line

// Signature Brand Accent System (Icon #21CDB8)
val WePlayTealPrimary = Color(0xFF21CDB8)       // Icon Primary Electric Teal Accent
val WePlayTealGlow = Color(0xFF3DE8D2)          // Luminous Cyan Peak Corona Glow
val WePlayTealDark = Color(0xFF107A6D)          // Deep Metallic Precision Shadow
val WePlayTealSubtle = Color(0x2221CDB8)        // Diffuse Volumetric Ambient Halo

// Aliases mapped directly to #21CDB8 to maintain 100% theme consistency
val WePlayOrangePrimary = WePlayTealPrimary
val WePlayOrangeGlow = WePlayTealGlow
val WePlayOrangeDark = WePlayTealDark
val WePlayOrangeSubtle = WePlayTealSubtle

// Electric Connectivity Accent System
val WePlayElectricBlue = Color(0xFF21CDB8)      // Unified Brand Radar & Nodes
val WePlayElectricCyan = Color(0xFF3DE8D2)      // PTP Phase Lock / Ray
val WePlayElectricBlueSubtle = Color(0x2421CDB8)

// Hi-Fi Telemetry & Signal Enamel
val WePlayStatusGreen = Color(0xFF21CDB8)       // Verified Connection / Sync Locked
val WePlayStatusRed = Color(0xFFFF3B56)         // LIVE Broadcast / Critical Stop
val WePlayStatusAmber = Color(0xFFFFB300)       // Hi-Res Audio / Buffer Alert

// High-Contrast Typographic Enamel
val WePlayTextPrimary = Color(0xFFFFFFFF)       // Crisp White Milled Typography
val WePlayTextSecondary = Color(0xFF8E9BAE)     // Subdued Studio Slate Label
val WePlayTextMuted = Color(0xFF546477)         // Technical Telemetry Annotations

// Atelier / Instrumentarium Compatibility Aliases
val AtelierObsidian = WePlayObsidian
val AtelierGunmetal = WePlaySurface
val AtelierTray = WePlaySurfaceElevated
val AtelierChamfer = WePlayBorder
val AtelierTextPrimary = WePlayTextPrimary
val AtelierTextSecondary = WePlayTextSecondary
val SignalVermilion = WePlayTealPrimary
val SignalVermilionSubtle = WePlayTealSubtle
val ElectricCobalt = WePlayElectricBlue
val ElectricCobaltSubtle = WePlayElectricBlueSubtle
val CalibratedGold = WePlayStatusAmber
val CalibratedGoldSubtle = Color(0x24FFB300)
val PrecisionCyan = WePlayTealPrimary
val PrecisionCyanSubtle = WePlayTealSubtle
val HardwareBarred = WePlayStatusRed

// Light Palette (Drafting / High Ambient Light Mode)
val CanvasLight = Color(0xFFF2F4F8)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceSubtleLight = Color(0xFFE8ECF2)
val TextPrimaryLight = Color(0xFF0A0E14)
val TextSecondaryLight = Color(0xFF5F6E80)
val SeparatorLight = Color(0xFFD3DAE4)

// Dark Palette
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
    accent = WePlayTealPrimary,
    accentSubtle = WePlayTealSubtle,
    signalVermilion = WePlayTealPrimary,
    electricCobalt = WePlayElectricBlue,
    calibratedGold = WePlayStatusAmber,
    precisionCyan = WePlayTealPrimary,
    coral = Color(0xFF21CDB8),
    blue = Color(0xFF1B8A7C),
    lavender = Color(0xFF264052),
    gold = WePlayStatusAmber,
    warning = Color(0xFFE5A100),
    error = WePlayStatusRed,
    success = WePlayTealPrimary,
    isDark = false
)

val DarkPalette = WePlayColorPalette(
    canvas = CanvasDark,
    surface = SurfaceDark,
    surfaceSubtle = SurfaceSubtleDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    separator = SeparatorDark,
    accent = WePlayTealPrimary,
    accentSubtle = WePlayTealSubtle,
    signalVermilion = WePlayTealPrimary,
    electricCobalt = WePlayElectricBlue,
    calibratedGold = WePlayStatusAmber,
    precisionCyan = WePlayTealPrimary,
    coral = Color(0xFF21CDB8),
    blue = Color(0xFF3DE8D2),
    lavender = Color(0xFF1A3342),
    gold = WePlayStatusAmber,
    warning = WePlayStatusAmber,
    error = WePlayStatusRed,
    success = WePlayTealPrimary,
    isDark = true
)
