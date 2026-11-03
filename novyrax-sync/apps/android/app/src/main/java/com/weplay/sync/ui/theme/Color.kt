package com.weplay.sync.ui.theme

import androidx.compose.ui.graphics.Color

// Atelier Instrumentarium & Monolithic Precision — Core Tokens
val AtelierObsidian = Color(0xFF0B0E14)       // Deep Canvas Ground
val AtelierGunmetal = Color(0xFF131822)       // Modular Chassis
val AtelierTray = Color(0xFF1A212D)           // Recessed Module Trays
val AtelierChamfer = Color(0xFF243042)        // Chamfer Edge / Milling Border
val AtelierTextPrimary = Color(0xFFF2F5F8)    // Milled Aluminum Lettering
val AtelierTextSecondary = Color(0xFF7E8A98)  // Laser-etched Annotations

// High-Visibility Functional Signal Enamel
val SignalVermilion = Color(0xFFFF3B00)       // Active Transmission / Live Conduit
val SignalVermilionSubtle = Color(0x24FF3B00)
val ElectricCobalt = Color(0xFF1A62FF)        // PTP Clock Sync / Phase Lock
val ElectricCobaltSubtle = Color(0x241A62FF)
val CalibratedGold = Color(0xFFE5A93C)        // 24-bit / 96kHz Studio Badge
val CalibratedGoldSubtle = Color(0x24E5A93C)
val PrecisionCyan = Color(0xFF00E5C8)         // Optical Telemetry / Active Indicator
val PrecisionCyanSubtle = Color(0x2400E5C8)
val HardwareBarred = Color(0xFFFF2A40)        // DRM / Widevine Boundary Barred

// Light Palette (Technical Studio Drafting Mode)
val CanvasLight = Color(0xFFF0EDE5)
val SurfaceLight = Color(0xFFFAF8F5)
val SurfaceSubtleLight = Color(0xFFE5E1D8)
val TextPrimaryLight = Color(0xFF0D1117)
val TextSecondaryLight = Color(0xFF6B7280)
val SeparatorLight = Color(0xFFD1CBC0)

// Dark Palette (Atelier Instrumentarium Standard Mode)
val CanvasDark = AtelierObsidian
val SurfaceDark = AtelierGunmetal
val SurfaceSubtleDark = AtelierTray
val TextPrimaryDark = AtelierTextPrimary
val TextSecondaryDark = AtelierTextSecondary
val SeparatorDark = AtelierChamfer

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
    accent = SignalVermilion,
    accentSubtle = SignalVermilionSubtle,
    signalVermilion = SignalVermilion,
    electricCobalt = ElectricCobalt,
    calibratedGold = CalibratedGold,
    precisionCyan = PrecisionCyan,
    coral = SignalVermilion,
    blue = ElectricCobalt,
    lavender = Color(0xFF8B7CF6),
    gold = CalibratedGold,
    warning = Color(0xFFF59E0B),
    error = HardwareBarred,
    success = PrecisionCyan,
    isDark = false
)

val DarkPalette = WePlayColorPalette(
    canvas = CanvasDark,
    surface = SurfaceDark,
    surfaceSubtle = SurfaceSubtleDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    separator = SeparatorDark,
    accent = SignalVermilion,
    accentSubtle = SignalVermilionSubtle,
    signalVermilion = SignalVermilion,
    electricCobalt = ElectricCobalt,
    calibratedGold = CalibratedGold,
    precisionCyan = PrecisionCyan,
    coral = SignalVermilion,
    blue = ElectricCobalt,
    lavender = Color(0xFF8B7CF6),
    gold = CalibratedGold,
    warning = Color(0xFFF59E0B),
    error = HardwareBarred,
    success = PrecisionCyan,
    isDark = true
)
