package com.smartremote.pro.domain.models

enum class ProfileRole {
    ADMIN,
    PARENT,
    CHILD,
    GUEST,
    ELDER
}

data class FamilyProfile(
    val id: String,
    val name: String,
    val role: ProfileRole,
    val avatar: String = "avatar_default",
    val pinHash: String? = null,
    val isElderMode: Boolean = false,
    val restrictedChannelCategories: List<String> = emptyList(),
    val maxDailyScreenMinutes: Int = 0, // 0 = unlimited
    val canAccessSettings: Boolean = true,
    val canModifyMacros: Boolean = true
)
