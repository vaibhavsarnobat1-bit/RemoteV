package com.smartremote.pro.data.repository

import com.smartremote.pro.core.ir.IRCodeDatabase
import com.smartremote.pro.core.network.DeviceDiscovery
import com.smartremote.pro.data.local.database.dao.DeviceDao
import com.smartremote.pro.data.local.database.entities.DeviceEntity
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.RemoteCommand
import com.smartremote.pro.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepositoryImpl(
    private val deviceDao: DeviceDao,
    private val irCodeDatabase: IRCodeDatabase,
    private val deviceDiscovery: DeviceDiscovery
) : DeviceRepository {

    override fun getAllDevices(): Flow<List<Device>> {
        return deviceDao.getAllDevices().map { list -> list.map { it.toDomain() } }
    }

    override fun getDevicesByRoom(room: String): Flow<List<Device>> {
        return deviceDao.getDevicesByRoom(room).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getDeviceById(id: String): Device? {
        return deviceDao.getDeviceById(id)?.toDomain()
    }

    override suspend fun insertDevice(device: Device): Long {
        return deviceDao.insertDevice(DeviceEntity.fromDomain(device))
    }

    override suspend fun updateDevice(device: Device) {
        deviceDao.updateDevice(DeviceEntity.fromDomain(device))
    }

    override suspend fun deleteDevice(id: String) {
        deviceDao.deleteDevice(id)
    }

    override suspend fun getCommandForDevice(deviceId: String, commandName: String): RemoteCommand? {
        val device = getDeviceById(deviceId) ?: return null
        val hex = irCodeDatabase.getCommandCode(device.brand, device.type, commandName)
        return if (hex != null) {
            RemoteCommand(id = "${deviceId}_$commandName", name = commandName, hexCode = hex)
        } else {
            null
        }
    }

    override suspend fun getPreloadedCommands(brand: String, deviceType: DeviceType): Map<String, String> {
        return irCodeDatabase.getAllCommands(brand, deviceType)
    }

    override suspend fun discoverNetworkDevices(): List<Device> {
        val discovered = deviceDiscovery.discoverDevices()
        if (discovered.isNotEmpty()) {
            deviceDao.insertDevices(discovered.map { DeviceEntity.fromDomain(it) })
        }
        return discovered
    }
}
