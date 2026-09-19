package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.core.gesture.RemoteGesture
import com.smartremote.pro.core.network.SmartTvClient
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.usecases.SendDeviceCommandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RemoteUiState(
    val currentDevice: Device? = null,
    val isLoading: Boolean = true,
    val lastDispatchedCommand: String? = null,
    val isElderMode: Boolean = false,
    val acTemperature: Int = 24,
    val currentInputSource: String = "HDMI 1"
)

class RemoteViewModel(
    private val deviceRepository: DeviceRepository,
    private val sendDeviceCommandUseCase: SendDeviceCommandUseCase,
    private val smartTvClient: SmartTvClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    fun loadDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val device = deviceRepository.getDeviceById(deviceId)
            _uiState.value = _uiState.value.copy(currentDevice = device, isLoading = false)
        }
    }

    fun sendCommand(commandName: String) {
        val device = _uiState.value.currentDevice ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(lastDispatchedCommand = commandName)
            sendDeviceCommandUseCase(device, commandName)
        }
    }

    fun handleGesture(gesture: RemoteGesture) {
        when (gesture) {
            RemoteGesture.SWIPE_UP -> sendCommand("volUp")
            RemoteGesture.SWIPE_DOWN -> sendCommand("volDown")
            RemoteGesture.SWIPE_RIGHT -> sendCommand("chUp")
            RemoteGesture.SWIPE_LEFT -> sendCommand("chDown")
            RemoteGesture.TAP -> sendCommand("ok")
            RemoteGesture.CIRCLE_MENU -> sendCommand("menu")
        }
    }

    fun adjustAcTemperature(delta: Int) {
        val newTemp = (_uiState.value.acTemperature + delta).coerceIn(16, 30)
        _uiState.value = _uiState.value.copy(acTemperature = newTemp)
        if (delta > 0) {
            sendCommand("tempUp")
        } else {
            sendCommand("tempDown")
        }
    }

    fun launchStreamingApp(appName: String) {
        val device = _uiState.value.currentDevice ?: return
        viewModelScope.launch {
            if (!device.ipAddress.isNullOrBlank()) {
                smartTvClient.launchApp(device.ipAddress, device.port, device.brand, appName)
            } else {
                sendCommand(appName.lowercase())
            }
        }
    }
}
