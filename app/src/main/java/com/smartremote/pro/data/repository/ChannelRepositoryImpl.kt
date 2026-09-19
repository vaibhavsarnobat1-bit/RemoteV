package com.smartremote.pro.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartremote.pro.data.local.database.dao.ChannelDao
import com.smartremote.pro.data.local.database.entities.ChannelEntity
import com.smartremote.pro.domain.models.Channel
import com.smartremote.pro.domain.repository.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class ChannelRepositoryImpl(
    private val context: Context,
    private val channelDao: ChannelDao
) : ChannelRepository {

    override fun getAllChannels(): Flow<List<Channel>> {
        return channelDao.getAllChannels().map { list -> list.map { it.toDomain() } }
    }

    override fun getFavoriteChannels(): Flow<List<Channel>> {
        return channelDao.getFavoriteChannels().map { list -> list.map { it.toDomain() } }
    }

    override fun getRecentChannels(): Flow<List<Channel>> {
        return channelDao.getRecentChannels().map { list -> list.map { it.toDomain() } }
    }

    override fun getChannelsByCategory(category: String): Flow<List<Channel>> {
        return channelDao.getChannelsByCategory(category).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleFavorite(channelNumber: Int, isFavorite: Boolean) {
        channelDao.updateFavorite(channelNumber, isFavorite)
    }

    override suspend fun markChannelWatched(channelNumber: Int) {
        channelDao.updateLastWatched(channelNumber, System.currentTimeMillis())
    }

    override suspend fun loadInitialChannels(): Int = withContext(Dispatchers.IO) {
        val existingCount = channelDao.getChannelCount()
        if (existingCount > 0) return@withContext existingCount

        try {
            context.assets.open("channels/airtel_channels.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<ChannelDto>>() {}.type
                    val list: List<ChannelDto> = Gson().fromJson(reader, type)

                    val entities = list.map { dto ->
                        ChannelEntity(
                            number = dto.number,
                            name = dto.name,
                            category = dto.category,
                            language = dto.language ?: "Hindi",
                            logoAsset = dto.logo ?: "",
                            isFavorite = false,
                            lastWatchedTimestamp = null
                        )
                    }
                    channelDao.insertChannels(entities)
                    return@withContext entities.size
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private data class ChannelDto(
        val number: Int,
        val name: String,
        val category: String,
        val language: String?,
        val logo: String?
    )
}
