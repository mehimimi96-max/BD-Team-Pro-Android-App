package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PinkBlackColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = InkBlack,
    primaryContainer = PinkBgDeep,
    onPrimaryContainer = InkBlack,
    secondary = PinkAccent,
    onSecondary = InkBlack,
    secondaryContainer = PinkCardAlt,
    onSecondaryContainer = InkBlack,
    tertiary = PinkBorder,
    onTertiary = PinkCard,
    background = PinkBg,
    onBackground = InkBlack,
    surface = PinkCard,
    onSurface = InkBlack,
    surfaceVariant = PinkCardAlt,
    onSurfaceVariant = InkBlackSecondary,
    outline = PinkBorder,
    outlineVariant = PinkPrimary,
    error = StatusRedInk,
    onError = PinkCard,
    errorContainer = StatusRedBg,
    onErrorContainer = StatusRedInk
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BDEarningTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PinkBlackColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
