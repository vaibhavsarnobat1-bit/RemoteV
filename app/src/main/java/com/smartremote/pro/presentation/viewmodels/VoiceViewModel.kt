package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.core.voice.TextToSpeechManager
import com.smartremote.pro.core.voice.VoiceRecognizer
import com.smartremote.pro.core.voice.VoiceState
import com.smartremote.pro.data.repository.SettingsRepositoryImpl
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.VoiceCommandAction
import com.smartremote.pro.domain.models.VoiceCommandResult
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.usecases.ExecuteMacroUseCase
import com.smartremote.pro.domain.usecases.ParseVoiceCommandUseCase
import com.smartremote.pro.domain.usecases.SendDeviceCommandUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class VoiceUiState(
    val isListening: Boolean = false,
    val normalizedAudioLevel: Float = 0f,
    val recognizedText: String = "",
    val feedbackMessage: String = "Tap microphone to speak (English / Hindi)",
    val remainingVoiceCommands: Int = 5,
    val isExecuting: Boolean = false
)

class VoiceViewModel(
    private val voiceRecognizer: VoiceRecognizer,
    private val parseVoiceCommandUseCase: ParseVoiceCommandUseCase,
    private val sendDeviceCommandUseCase: SendDeviceCommandUseCase,
    private val executeMacroUseCase: ExecuteMacroUseCase,
    private val deviceRepository: DeviceRepository,
    private val settingsRepository: SettingsRepositoryImpl,
    private val textToSpeechManager: TextToSpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var listeningJob: Job? = null

    init {
        _uiState.value = _uiState.value.copy(
            remainingVoiceCommands = settingsRepository.getRemainingVoiceCommands()
        )
    }

    fun startListening() {
        if (!settingsRepository.isPremium() && settingsRepository.getRemainingVoiceCommands() <= 0) {
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "Daily free limit reached (5 commands/day). Upgrade to Premium for unlimited voice control!"
            )
            return
        }

        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            voiceRecognizer.startListening().collect { state ->
                when (state) {
                    is VoiceState.Idle -> {
                        _uiState.value = _uiState.value.copy(isListening = false, normalizedAudioLevel = 0f)
                    }
                    is VoiceState.Ready -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = true,
                            feedbackMessage = "Listening... बोलिए..."
                        )
                    }
                    is VoiceState.Listening -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = true,
                            normalizedAudioLevel = state.rmsDb
                        )
                    }
                    is VoiceState.Partial -> {
                        _uiState.value = _uiState.value.copy(recognizedText = state.text)
                    }
                    is VoiceState.Final -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            recognizedText = state.text,
                            normalizedAudioLevel = 0f
                        )
                        processCommand(state.text)
                    }
                    is VoiceState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            normalizedAudioLevel = 0f,
                            feedbackMessage = state.message
                        )
                    }
                }
            }
        }
    }

    fun stopListening() {
        voiceRecognizer.stop()
        listeningJob?.cancel()
        _uiState.value = _uiState.value.copy(isListening = false, normalizedAudioLevel = 0f)
    }

    private fun processCommand(rawText: String) {
        if (rawText.isBlank()) return

        settingsRepository.recordVoiceCommandUsed()
        _uiState.value = _uiState.value.copy(
            remainingVoiceCommands = settingsRepository.getRemainingVoiceCommands(),
            isExecuting = true
        )

        val result: VoiceCommandResult = parseVoiceCommandUseCase(rawText)
        _uiState.value = _uiState.value.copy(
            feedbackMessage = result.feedbackMessage,
            isExecuting = false
        )

        // Spoken TTS feedback
        textToSpeechManager.speak(result.feedbackMessage, result.detectedLanguage)

        // Dispatch action to targeted device
        viewModelScope.launch {
            val devices = deviceRepository.getAllDevices().firstOrNull() ?: emptyList()
            val targetDevice = if (result.targetDeviceType != null) {
                devices.find { it.type == result.targetDeviceType } ?: devices.firstOrNull()
            } else {
                devices.firstOrNull()
            }

            if (targetDevice != null) {
                when (val action = result.action) {
                    is VoiceCommandAction.PowerOn, is VoiceCommandAction.PowerToggle -> {
                        sendDeviceCommandUseCase(targetDevice, "power")
                    }
                    is VoiceCommandAction.PowerOff -> {
                        sendDeviceCommandUseCase(targetDevice, "power")
                    }
                    is VoiceCommandAction.VolumeUp -> {
                        sendDeviceCommandUseCase(targetDevice, "volUp")
                    }
                    is VoiceCommandAction.VolumeDown -> {
                        sendDeviceCommandUseCase(targetDevice, "volDown")
                    }
                    is VoiceCommandAction.SetVolume -> {
                        sendDeviceCommandUseCase(targetDevice, "volUp")
                    }
                    is VoiceCommandAction.MuteToggle -> {
                        sendDeviceCommandUseCase(targetDevice, "mute")
                    }
                    is VoiceCommandAction.ChannelUp -> {
                        sendDeviceCommandUseCase(targetDevice, "chUp")
                    }
                    is VoiceCommandAction.ChannelDown -> {
                        sendDeviceCommandUseCase(targetDevice, "chDown")
                    }
                    is VoiceCommandAction.ChangeChannel -> {
                        sendDeviceCommandUseCase(targetDevice, "chUp")
                    }
                    is VoiceCommandAction.OpenApp -> {
                        sendDeviceCommandUseCase(targetDevice, action.appName.lowercase())
                    }
                    is VoiceCommandAction.PlayPause -> {
                        sendDeviceCommandUseCase(targetDevice, "ok")
                    }
                    else -> {}
                }
            }
        }
    }
}
