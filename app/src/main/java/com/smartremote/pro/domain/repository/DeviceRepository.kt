package com.smartremote.pro.domain.repository

import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.RemoteCommand
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllDevices(): Flow<List<Device>>
    fun getDevicesByRoom(room: String): Flow<List<Device>>
    suspend fun getDeviceById(id: String): Device?
    suspend fun insertDevice(device: Device): Long
    suspend fun updateDevice(device: Device)
    suspend fun deleteDevice(id: String)
    suspend fun getCommandForDevice(deviceId: String, commandName: String): RemoteCommand?
    suspend fun getPreloadedCommands(brand: String, deviceType: DeviceType): Map<String, String>
    suspend fun discoverNetworkDevices(): List<Device>
}
