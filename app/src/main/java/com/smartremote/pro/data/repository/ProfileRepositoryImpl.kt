package com.smartremote.pro.data.repository

import com.smartremote.pro.data.local.database.dao.ProfileDao
import com.smartremote.pro.data.local.database.entities.ProfileEntity
import com.smartremote.pro.data.local.preferences.EncryptedPreferenceManager
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.ProfileRole
import com.smartremote.pro.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val preferenceManager: EncryptedPreferenceManager
) : ProfileRepository {

    override fun getAllProfiles(): Flow<List<FamilyProfile>> {
        return profileDao.getAllProfiles().map { list -> list.map { it.toDomain() } }
    }

    override fun getActiveProfile(): Flow<FamilyProfile> {
        val activeId = preferenceManager.activeProfileId
        return profileDao.getAllProfiles().map { list ->
            list.find { it.id == activeId }?.toDomain()
                ?: list.firstOrNull()?.toDomain()
                ?: createDefaultAdmin()
        }
    }

    override suspend fun setActiveProfile(profileId: String) {
        preferenceManager.activeProfileId = profileId
    }

    override suspend fun saveProfile(profile: FamilyProfile) {
        profileDao.insertProfile(ProfileEntity.fromDomain(profile))
    }

    override suspend fun verifyPin(profileId: String, enteredPin: String): Boolean {
        val profile = profileDao.getProfileById(profileId)?.toDomain() ?: return false
        val storedHash = profile.pinHash ?: return true // no PIN required
        val inputHash = hashPin(enteredPin)
        return storedHash == inputHash
    }

    override suspend fun initializeDefaultProfiles() {
        if (profileDao.getProfileCount() > 0) return

        val admin = createDefaultAdmin()
        val parent = FamilyProfile(
            id = "profile_parent",
            name = "Mom & Dad",
            role = ProfileRole.PARENT,
            avatar = "avatar_parent"
        )
        val child = FamilyProfile(
            id = "profile_child",
            name = "Kids Mode",
            role = ProfileRole.CHILD,
            avatar = "avatar_child",
            restrictedChannelCategories = listOf("Movies", "News"),
            maxDailyScreenMinutes = 60,
            canAccessSettings = false,
            canModifyMacros = false
        )
        val elder = FamilyProfile(
            id = "profile_elder",
            name = "Grandparents",
            role = ProfileRole.ELDER,
            avatar = "avatar_elder",
            isElderMode = true,
            canAccessSettings = false
        )
        val guest = FamilyProfile(
            id = "profile_guest",
            name = "Guest",
            role = ProfileRole.GUEST,
            avatar = "avatar_guest",
            canAccessSettings = false,
            canModifyMacros = false
        )

        val list = listOf(admin, parent, child, elder, guest)
        profileDao.insertProfiles(list.map { ProfileEntity.fromDomain(it) })
    }

    private fun createDefaultAdmin(): FamilyProfile {
        return FamilyProfile(
            id = "admin_profile",
            name = "Admin",
            role = ProfileRole.ADMIN,
            avatar = "avatar_admin",
            pinHash = hashPin("1234") // default PIN 1234
        )
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
