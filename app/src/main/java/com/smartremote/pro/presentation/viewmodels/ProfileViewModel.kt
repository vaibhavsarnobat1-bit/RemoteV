package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.ProfileRole
import com.smartremote.pro.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profiles: List<FamilyProfile> = emptyList(),
    val activeProfile: FamilyProfile? = null,
    val isPinPromptVisible: Boolean = false,
    val pendingProfileSwitch: FamilyProfile? = null,
    val pinError: String? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _isPinPromptVisible = MutableStateFlow(false)
    private val _pendingProfileSwitch = MutableStateFlow<FamilyProfile?>(null)
    private val _pinError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        profileRepository.getAllProfiles(),
        profileRepository.getActiveProfile(),
        _isPinPromptVisible,
        _pendingProfileSwitch,
        _pinError
    ) { profiles, active, pinVisible, pending, error ->
        ProfileUiState(
            profiles = profiles,
            activeProfile = active,
            isPinPromptVisible = pinVisible,
            pendingProfileSwitch = pending,
            pinError = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ProfileUiState()
    )

    fun requestProfileSwitch(profile: FamilyProfile) {
        if (profile.role == ProfileRole.ADMIN && !profile.pinHash.isNullOrBlank()) {
            _pendingProfileSwitch.value = profile
            _isPinPromptVisible.value = true
            _pinError.value = null
        } else {
            switchProfileDirect(profile.id)
        }
    }

    fun submitPin(pin: String) {
        val pending = _pendingProfileSwitch.value ?: return
        viewModelScope.launch {
            val isValid = profileRepository.verifyPin(pending.id, pin)
            if (isValid) {
                switchProfileDirect(pending.id)
                dismissPinPrompt()
            } else {
                _pinError.value = "Incorrect PIN code. Try again."
            }
        }
    }

    fun dismissPinPrompt() {
        _isPinPromptVisible.value = false
        _pendingProfileSwitch.value = null
        _pinError.value = null
    }

    private fun switchProfileDirect(profileId: String) {
        viewModelScope.launch {
            profileRepository.setActiveProfile(profileId)
        }
    }
}
