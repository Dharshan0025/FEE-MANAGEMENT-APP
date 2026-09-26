package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalMaroonDarkPrimary,
    onPrimary = OnMaroonContainer,
    primaryContainer = MaroonDarkPrimaryContainer,
    onPrimaryContainer = MaroonPrimaryContainer,
    secondary = TempleGoldDarkSecondary,
    onSecondary = OnGoldContainer,
    secondaryContainer = GoldDarkSecondaryContainer,
    onSecondaryContainer = GoldSecondaryContainer,
    tertiary = EmeraldDarkTertiary,
    onTertiary = OnEmeraldContainer,
    tertiaryContainer = EmeraldDarkTertiaryContainer,
    onTertiaryContainer = EmeraldContainer,
    background = ClassicalBackgroundDark,
    surface = ClassicalSurfaceDark,
    surfaceVariant = ClassicalSurfaceVariantDark,
    onSurface = ClassicalOnSurfaceDark,
    outline = ClassicalOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalMaroonPrimary,
    onPrimary = OnMaroon,
    primaryContainer = MaroonPrimaryContainer,
    onPrimaryContainer = OnMaroonContainer,
    secondary = TempleGoldSecondary,
    onSecondary = OnGold,
    secondaryContainer = GoldSecondaryContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = EmeraldTertiary,
    onTertiary = OnEmerald,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = OnEmeraldContainer,
    background = ClassicalBackgroundLight,
    surface = ClassicalSurfaceLight,
    surfaceVariant = ClassicalSurfaceVariantLight,
    onSurface = ClassicalOnSurfaceLight,
    outline = ClassicalOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature classical Indian arts branding palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
