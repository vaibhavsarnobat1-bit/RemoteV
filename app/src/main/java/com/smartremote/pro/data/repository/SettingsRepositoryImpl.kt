package com.smartremote.pro.data.repository

import com.google.gson.Gson
import com.smartremote.pro.data.local.preferences.EncryptedPreferenceManager
import com.smartremote.pro.data.remote.models.CloudBackupPayload
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.Macro

class SettingsRepositoryImpl(
    private val preferenceManager: EncryptedPreferenceManager
) {

    fun isPremium(): Boolean = preferenceManager.isPremiumUser

    fun setPremium(isPremium: Boolean) {
        preferenceManager.isPremiumUser = isPremium
    }

    fun getRemainingVoiceCommands(): Int {
        if (isPremium()) return Int.MAX_VALUE
        val used = preferenceManager.dailyVoiceCommandsUsed
        return (5 - used).coerceAtLeast(0)
    }

    fun recordVoiceCommandUsed() {
        if (!isPremium()) {
            preferenceManager.dailyVoiceCommandsUsed++
        }
    }

    fun getSelectedTheme(): String = preferenceManager.selectedTheme

    fun setTheme(theme: String) {
        preferenceManager.selectedTheme = theme
    }

    fun isHapticsEnabled(): Boolean = preferenceManager.isHapticsEnabled

    fun setHapticsEnabled(enabled: Boolean) {
        preferenceManager.isHapticsEnabled = enabled
    }

    fun exportConfigurationJson(
        devices: List<Device>,
        macros: List<Macro>,
        profiles: List<FamilyProfile>
    ): String {
        val payload = CloudBackupPayload(
            userId = "user_local_backup",
            devices = devices,
            macros = macros,
            profiles = profiles
        )
        return Gson().toJson(payload)
    }

    fun importConfigurationJson(json: String): CloudBackupPayload? {
        return try {
            Gson().fromJson(json, CloudBackupPayload::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
