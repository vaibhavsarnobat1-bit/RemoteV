package com.smartremote.pro.domain.models

enum class DeviceType(val displayName: String, val category: String) {
    TV("Smart TV", "Television"),
    AC("Air Conditioner", "Climate"),
    SET_TOP_BOX("Set-Top Box / DTH", "Media"),
    SOUNDBAR("Sound System", "Audio"),
    DVD("DVD / Blu-ray", "Media"),
    PROJECTOR("Projector", "Display")
}

enum class ConnectionType {
    IR,
    WIFI,
    BLUETOOTH,
    HYBRID
}
