package com.smartremote.pro.domain.repository

import com.smartremote.pro.domain.models.Channel
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getAllChannels(): Flow<List<Channel>>
    fun getFavoriteChannels(): Flow<List<Channel>>
    fun getRecentChannels(): Flow<List<Channel>>
    fun getChannelsByCategory(category: String): Flow<List<Channel>>
    suspend fun toggleFavorite(channelNumber: Int, isFavorite: Boolean)
    suspend fun markChannelWatched(channelNumber: Int)
    suspend fun loadInitialChannels(): Int
}
