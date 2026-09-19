package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.usecases.DiscoverNetworkDevicesUseCase
import com.smartremote.pro.domain.usecases.SendDeviceCommandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val rooms: List<String> = listOf("All", "Living Room", "Bedroom", "Kitchen"),
    val selectedRoom: String = "All",
    val isScanning: Boolean = false,
    val canAddDevice: Boolean = true, // Free tier: max 3 devices
    val errorMessage: String? = null
)

class HomeViewModel(
    private val deviceRepository: DeviceRepository,
    private val sendDeviceCommandUseCase: SendDeviceCommandUseCase,
    private val discoverNetworkDevicesUseCase: DiscoverNetworkDevicesUseCase,
    private val isPremiumUser: Boolean = false
) : ViewModel() {

    private val _selectedRoom = MutableStateFlow("All")
    private val _isScanning = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        deviceRepository.getAllDevices(),
        _selectedRoom,
        _isScanning,
        _errorMessage
    ) { allDevices, room, scanning, error ->
        val filtered = if (room == "All") allDevices else allDevices.filter { it.room == room }
        val canAdd = isPremiumUser || allDevices.size < 3

        HomeUiState(
            devices = filtered,
            selectedRoom = room,
            isScanning = scanning,
            canAddDevice = canAdd,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    fun selectRoom(room: String) {
        _selectedRoom.value = room
    }

    fun toggleDevicePower(device: Device) {
        viewModelScope.launch {
            sendDeviceCommandUseCase(device, "power")
        }
    }

    fun scanNetworkDevices() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                discoverNetworkDevicesUseCase()
            } catch (e: Exception) {
                _errorMessage.value = "Device scan error: ${e.message}"
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
