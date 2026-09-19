package com.smartremote.pro.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Inter Typography scale
val Typography = Typography(
    // H1: 28sp (600 weight)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold, // 600 weight
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = TextPrimaryDark
    ),
    // H2: 24sp (600 weight)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold, // 600 weight
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = TextPrimaryDark
    ),
    // H3: 20sp (600 weight)
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold, // 600 weight
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = TextPrimaryDark
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = TextPrimaryDark
    ),
    // Body: 16sp (400 weight)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal, // 400 weight
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = TextPrimaryDark
    ),
    // Caption: 14sp (400 weight)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal, // 400 weight
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextSecondary
    ),
    // Small: 12sp (400 weight)
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextSecondary
    ),
    // Button: 14-16sp (500 weight)
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium, // 500 weight
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = TextPrimaryDark
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextSecondary
    )
)
