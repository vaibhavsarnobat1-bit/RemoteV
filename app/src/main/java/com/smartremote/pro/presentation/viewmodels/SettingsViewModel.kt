package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.core.utils.FindMyRemoteBeep
import com.smartremote.pro.data.repository.SettingsRepositoryImpl
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.repository.MacroRepository
import com.smartremote.pro.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isPremium: Boolean = false,
    val selectedTheme: String = "AMOLED",
    val isHapticsEnabled: Boolean = true,
    val isBeeping: Boolean = false,
    val backupJsonOutput: String? = null,
    val backupStatusMessage: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepositoryImpl,
    private val deviceRepository: DeviceRepository,
    private val macroRepository: MacroRepository,
    private val profileRepository: ProfileRepository,
    private val findMyRemoteBeep: FindMyRemoteBeep
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isPremium = settingsRepository.isPremium(),
            selectedTheme = settingsRepository.getSelectedTheme(),
            isHapticsEnabled = settingsRepository.isHapticsEnabled()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun togglePremium(isPremium: Boolean) {
        settingsRepository.setPremium(isPremium)
        _uiState.value = _uiState.value.copy(isPremium = isPremium)
    }

    fun setTheme(theme: String) {
        settingsRepository.setTheme(theme)
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }

    fun setHaptics(enabled: Boolean) {
        settingsRepository.setHapticsEnabled(enabled)
        _uiState.value = _uiState.value.copy(isHapticsEnabled = enabled)
    }

    fun triggerFindMyRemote() {
        if (_uiState.value.isBeeping) {
            findMyRemoteBeep.stopBeeping()
            _uiState.value = _uiState.value.copy(isBeeping = false)
        } else {
            findMyRemoteBeep.startBeeping(10)
            _uiState.value = _uiState.value.copy(isBeeping = true)
        }
    }

    fun exportBackup() {
        viewModelScope.launch {
            val devices = deviceRepository.getAllDevices().firstOrNull() ?: emptyList()
            val macros = macroRepository.getAllMacros().firstOrNull() ?: emptyList()
            val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()

            val json = settingsRepository.exportConfigurationJson(devices, macros, profiles)
            _uiState.value = _uiState.value.copy(
                backupJsonOutput = json,
                backupStatusMessage = "Backup successfully generated (${devices.size} devices, ${macros.size} macros)"
            )
        }
    }
}
