package com.smartremote.pro.data.local.preferences

data class UserPreferences(
    val activeProfileId: String,
    val isPremium: Boolean,
    val voiceCommandsRemaining: Int,
    val selectedTheme: String,
    val hapticsEnabled: Boolean
)
