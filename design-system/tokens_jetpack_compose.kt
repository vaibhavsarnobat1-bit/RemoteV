// ==========================================================================
// SmartRemote Pro - Android Jetpack Compose Material 3 Design System
// Target: Indian Users (Hindi + English)
// Platform: Android (Material Design 3)
// ==========================================================================

package com.smartremote.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================================
// 1. COLOR TOKENS
// ==========================================================================

// Primary Brand Colors
val DeepBlue = Color(0xFF1E3A8A)        // Trust, technology
val ElectricBlue = Color(0xFF3B82F6)    // Interactive elements, active states
val AccentOrange = Color(0xFFF97316)    // CTAs, Pro badges, warnings

// Neutral Colors
val BackgroundDark = Color(0xFF0F172A)  // Dark OLED background
val BackgroundLight = Color(0xFFF8FAFC) // Light mode background
val SurfaceDark = Color(0xFF1E293B)     // Remote body, cards
val SurfaceLight = Color(0xFFFFFFFF)    // Light mode cards
val SurfaceElevatedDark = Color(0xFF283548)
val TextPrimaryDark = Color(0xFFF1F5F9) // High emphasis (15.3:1 contrast)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondary = Color(0xFF94A3B8)   // Captions, metadata
val BorderDark = Color(0xFF334155)
val BorderLight = Color(0xFFE2E8F0)

// Semantic Colors
val SuccessGreen = Color(0xFF10B981)    // Online dot, speech confirm
val ErrorRed = Color(0xFFEF4444)        // Power button, offline status
val WarningYellow = Color(0xFFF59E0B)   // Speech highlight
val InfoBlue = Color(0xFF06B6D4)        // AC cooling badges

// Material 3 Color Schemes
val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = DeepBlue,
    onPrimaryContainer = Color.White,
    secondary = AccentOrange,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    error = ErrorRed,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = Color.White,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = Color.White,
    secondary = AccentOrange,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = BorderLight,
    error = ErrorRed,
    onError = Color.White
)

// ==========================================================================
// 2. TYPOGRAPHY SCALE (INTER)
// ==========================================================================

val SmartRemoteTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// ==========================================================================
// 3. 4DP GRID & ELEVATION DIMENSIONS
// ==========================================================================

object SmartRemoteDimens {
    val MinTouchTarget = 48.dp
    val Spacing4 = 4.dp
    val Spacing8 = 8.dp
    val Spacing12 = 12.dp
    val Spacing16 = 16.dp
    val Spacing20 = 20.dp
    val Spacing24 = 24.dp
    val Spacing32 = 32.dp
    val Spacing48 = 48.dp

    val CardCornerRadius = 16.dp
    val ButtonCornerRadius = 12.dp
    val ChipCornerRadius = 20.dp

    val ElevationCard = 2.dp
    val ElevationDpad = 6.dp
    val ElevationModal = 12.dp
}

// ==========================================================================
// 4. THEME PROVIDER
// ==========================================================================

@Composable
fun SmartRemoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SmartRemoteTypography,
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(SmartRemoteDimens.CardCornerRadius),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}
