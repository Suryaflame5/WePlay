package com.weplay.sync.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Listening Atelier — Light Palette
val CanvasLight = Color(0xFFF4F1E9)
val SurfaceLight = Color(0xFFFFFDFC)
val SurfaceSubtleLight = Color(0xFFECE9E2)
val TextPrimaryLight = Color(0xFF17191E)
val TextSecondaryLight = Color(0xFF777D86)
val SeparatorLight = Color(0xFFE2DED5)

// Modern Listening Atelier — Dark Palette
val CanvasDark = Color(0xFF11151B)
val SurfaceDark = Color(0xFF1B222B)
val SurfaceSubtleDark = Color(0xFF242C37)
val TextPrimaryDark = Color(0xFFF4F2EB)
val TextSecondaryDark = Color(0xFFA8AFB8)
val SeparatorDark = Color(0xFF28313D)

// Expressive Atelier Accents
val AccentTeal = Color(0xFF24B9A6)
val AccentTealSubtle = Color(0x1F24B9A6)
val AccentCoral = Color(0xFFF27B58)
val AccentCoralSubtle = Color(0x1FF27B58)
val AccentBlue = Color(0xFF5878D9)
val AccentBlueSubtle = Color(0x1F5878D9)
val AccentLavender = Color(0xFF9A86C8)
val AccentLavenderSubtle = Color(0x1F9A86C8)
val AccentGold = Color(0xFFD0A34A)
val AccentGoldSubtle = Color(0x1FD0A34A)

// Functional State Colors
val StateWarning = Color(0xFFE67E22)
val StateWarningSubtle = Color(0x1FE67E22)
val StateError = Color(0xFFD9534F)
val StateErrorSubtle = Color(0x1FD9534F)
val StateSuccess = Color(0xFF2ECC71)

data class WePlayColorPalette(
    val canvas: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val separator: Color,
    val accent: Color,
    val accentSubtle: Color,
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
    accent = AccentTeal,
    accentSubtle = AccentTealSubtle,
    coral = AccentCoral,
    blue = AccentBlue,
    lavender = AccentLavender,
    gold = AccentGold,
    warning = StateWarning,
    error = StateError,
    success = StateSuccess,
    isDark = false
)

val DarkPalette = WePlayColorPalette(
    canvas = CanvasDark,
    surface = SurfaceDark,
    surfaceSubtle = SurfaceSubtleDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    separator = SeparatorDark,
    accent = AccentTeal,
    accentSubtle = AccentTealSubtle,
    coral = AccentCoral,
    blue = AccentBlue,
    lavender = AccentLavender,
    gold = AccentGold,
    warning = StateWarning,
    error = StateError,
    success = StateSuccess,
    isDark = true
)
