package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.ProfileRole

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String, // from ProfileRole.name
    val avatar: String,
    val pinHash: String?,
    val isElderMode: Boolean,
    val restrictedCategoriesCsv: String,
    val maxDailyScreenMinutes: Int,
    val canAccessSettings: Boolean,
    val canModifyMacros: Boolean
) {
    fun toDomain(): FamilyProfile {
        return FamilyProfile(
            id = id,
            name = name,
            role = runCatching { ProfileRole.valueOf(role) }.getOrDefault(ProfileRole.ADMIN),
            avatar = avatar,
            pinHash = pinHash,
            isElderMode = isElderMode,
            restrictedChannelCategories = if (restrictedCategoriesCsv.isBlank()) emptyList() else restrictedCategoriesCsv.split(","),
            maxDailyScreenMinutes = maxDailyScreenMinutes,
            canAccessSettings = canAccessSettings,
            canModifyMacros = canModifyMacros
        )
    }

    companion object {
        fun fromDomain(profile: FamilyProfile): ProfileEntity {
            return ProfileEntity(
                id = profile.id,
                name = profile.name,
                role = profile.role.name,
                avatar = profile.avatar,
                pinHash = profile.pinHash,
                isElderMode = profile.isElderMode,
                restrictedCategoriesCsv = profile.restrictedChannelCategories.joinToString(","),
                maxDailyScreenMinutes = profile.maxDailyScreenMinutes,
                canAccessSettings = profile.canAccessSettings,
                canModifyMacros = profile.canModifyMacros
            )
        }
    }
}
