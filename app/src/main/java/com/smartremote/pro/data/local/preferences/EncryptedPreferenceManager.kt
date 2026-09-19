package com.smartremote.pro.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPreferenceManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "smart_remote_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences("smart_remote_fallback_prefs", Context.MODE_PRIVATE)
    }

    var activeProfileId: String
        get() = sharedPreferences.getString(KEY_ACTIVE_PROFILE, "admin_profile") ?: "admin_profile"
        set(value) = sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, value).apply()

    var isPremiumUser: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_PREMIUM, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_PREMIUM, value).apply()

    var dailyVoiceCommandsUsed: Int
        get() = sharedPreferences.getInt(KEY_VOICE_COMMANDS_TODAY, 0)
        set(value) = sharedPreferences.edit().putInt(KEY_VOICE_COMMANDS_TODAY, value).apply()

    var selectedTheme: String
        get() = sharedPreferences.getString(KEY_THEME, "AMOLED") ?: "AMOLED"
        set(value) = sharedPreferences.edit().putString(KEY_THEME, value).apply()

    var isHapticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_HAPTICS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HAPTICS, value).apply()

    var isOnboardingCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_ONBOARDING, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_ONBOARDING, value).apply()

    companion object {
        private const val KEY_ACTIVE_PROFILE = "active_profile_id"
        private const val KEY_IS_PREMIUM = "is_premium_tier"
        private const val KEY_VOICE_COMMANDS_TODAY = "voice_cmds_count"
        private const val KEY_THEME = "app_theme_mode"
        private const val KEY_HAPTICS = "haptics_enabled"
        private const val KEY_ONBOARDING = "onboarding_completed"
    }
}
