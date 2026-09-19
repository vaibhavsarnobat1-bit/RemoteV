package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.domain.models.Channel
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.repository.ChannelRepository
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.usecases.SendDeviceCommandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AirtelXstreamUiState(
    val channels: List<Channel> = emptyList(),
    val favoriteChannels: List<Channel> = emptyList(),
    val recentChannels: List<Channel> = emptyList(),
    val categories: List<String> = listOf("All", "Entertainment", "Sports", "Movies", "News", "Kids", "Infotainment"),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val accountBalance: String = "₹149.00",
    val daysUntilRecharge: Int = 5,
    val activePackName: String = "Airtel Mega HD Plus (320 Channels)",
    val lastDispatchedKey: String? = null
)

class AirtelXstreamViewModel(
    private val channelRepository: ChannelRepository,
    private val deviceRepository: DeviceRepository,
    private val sendDeviceCommandUseCase: SendDeviceCommandUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _lastDispatchedKey = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AirtelXstreamUiState> = combine(
        channelRepository.getAllChannels(),
        channelRepository.getFavoriteChannels(),
        channelRepository.getRecentChannels(),
        _selectedCategory,
        _searchQuery,
        _lastDispatchedKey
    ) { allChannels, favs, recents, category, query, lastKey ->
        val filtered = allChannels.filter { channel ->
            (category == "All" || channel.category.equals(category, ignoreCase = true)) &&
                    (query.isBlank() || channel.name.contains(query, ignoreCase = true) || channel.number.toString().contains(query))
        }

        AirtelXstreamUiState(
            channels = filtered,
            favoriteChannels = favs,
            recentChannels = recents,
            selectedCategory = category,
            searchQuery = query,
            lastDispatchedKey = lastKey
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AirtelXstreamUiState()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun searchChannels(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            channelRepository.toggleFavorite(channel.number, !channel.isFavorite)
        }
    }

    fun tuneToChannel(channel: Channel) {
        viewModelScope.launch {
            channelRepository.markChannelWatched(channel.number)
            sendDthCommand("ok")
        }
    }

    fun sendDthCommand(commandName: String) {
        _lastDispatchedKey.value = commandName
        viewModelScope.launch {
            val airtelDevice = Device(
                id = "airtel_xstream_dth",
                name = "Airtel Xstream",
                type = DeviceType.SET_TOP_BOX,
                brand = "Airtel"
            )
            sendDeviceCommandUseCase(airtelDevice, commandName)
        }
    }
}
