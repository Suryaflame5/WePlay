package com.weplay.sync.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWePlayColors = staticCompositionLocalOf { LightPalette }
val LocalWePlayTypography = staticCompositionLocalOf { WePlayTypography() }
val LocalWePlaySpacing = staticCompositionLocalOf { WePlaySpacing() }

@Composable
fun WePlayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkPalette else LightPalette
    val typography = WePlayTypography()
    val spacing = WePlaySpacing()

    CompositionLocalProvider(
        LocalWePlayColors provides colors,
        LocalWePlayTypography provides typography,
        LocalWePlaySpacing provides spacing,
        content = content
    )
}

object WePlayTheme {
    val colors: WePlayColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalWePlayColors.current

    val typography: WePlayTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalWePlayTypography.current

    val spacing: WePlaySpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalWePlaySpacing.current
}
