package com.smartremote.pro.domain.repository

import com.smartremote.pro.domain.models.FamilyProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllProfiles(): Flow<List<FamilyProfile>>
    fun getActiveProfile(): Flow<FamilyProfile>
    suspend fun setActiveProfile(profileId: String)
    suspend fun saveProfile(profile: FamilyProfile)
    suspend fun verifyPin(profileId: String, enteredPin: String): Boolean
    suspend fun initializeDefaultProfiles()
}
