package com.smartremote.pro.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartremote.pro.data.local.database.entities.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY lastUsedTimestamp DESC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE room = :room ORDER BY lastUsedTimestamp DESC")
    fun getDevicesByRoom(room: String): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :id LIMIT 1")
    suspend fun getDeviceById(id: String): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun deleteDevice(id: String)

    @Query("SELECT COUNT(*) FROM devices")
    suspend fun getDeviceCount(): Int
}
