package com.smartremote.pro.domain.models

data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val brand: String,
    val model: String = "Universal",
    val connectionType: ConnectionType = ConnectionType.IR,
    val ipAddress: String? = null,
    val port: Int = 8080,
    val macAddress: String? = null,
    val room: String = "Living Room",
    val isOnline: Boolean = true,
    val isFavorite: Boolean = false,
    val isElderModeCompatible: Boolean = true,
    val customIcon: String? = null,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
