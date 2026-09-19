package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartremote.pro.domain.models.ConnectionType
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // from DeviceType.name
    val brand: String,
    val model: String,
    val connectionType: String, // from ConnectionType.name
    val ipAddress: String?,
    val port: Int,
    val macAddress: String?,
    val room: String,
    val isOnline: Boolean,
    val isFavorite: Boolean,
    val isElderModeCompatible: Boolean,
    val customIcon: String?,
    val lastUsedTimestamp: Long
) {
    fun toDomain(): Device {
        return Device(
            id = id,
            name = name,
            type = runCatching { DeviceType.valueOf(type) }.getOrDefault(DeviceType.TV),
            brand = brand,
            model = model,
            connectionType = runCatching { ConnectionType.valueOf(connectionType) }.getOrDefault(ConnectionType.IR),
            ipAddress = ipAddress,
            port = port,
            macAddress = macAddress,
            room = room,
            isOnline = isOnline,
            isFavorite = isFavorite,
            isElderModeCompatible = isElderModeCompatible,
            customIcon = customIcon,
            lastUsedTimestamp = lastUsedTimestamp
        )
    }

    companion object {
        fun fromDomain(device: Device): DeviceEntity {
            return DeviceEntity(
                id = device.id,
                name = device.name,
                type = device.type.name,
                brand = device.brand,
                model = device.model,
                connectionType = device.connectionType.name,
                ipAddress = device.ipAddress,
                port = device.port,
                macAddress = device.macAddress,
                room = device.room,
                isOnline = device.isOnline,
                isFavorite = device.isFavorite,
                isElderModeCompatible = device.isElderModeCompatible,
                customIcon = device.customIcon,
                lastUsedTimestamp = device.lastUsedTimestamp
            )
        }
    }
}
