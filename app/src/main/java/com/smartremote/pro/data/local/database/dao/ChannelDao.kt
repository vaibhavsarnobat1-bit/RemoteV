package com.smartremote.pro.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartremote.pro.data.local.database.entities.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY number ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 ORDER BY number ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE lastWatchedTimestamp IS NOT NULL ORDER BY lastWatchedTimestamp DESC LIMIT 10")
    fun getRecentChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE category = :category ORDER BY number ASC")
    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("UPDATE channels SET isFavorite = :isFav WHERE number = :channelNumber")
    suspend fun updateFavorite(channelNumber: Int, isFav: Boolean)

    @Query("UPDATE channels SET lastWatchedTimestamp = :timestamp WHERE number = :channelNumber")
    suspend fun updateLastWatched(channelNumber: Int, timestamp: Long)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int
}
