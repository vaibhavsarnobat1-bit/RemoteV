package com.smartremote.pro.data.remote.models

import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.Macro

data class CloudBackupPayload(
    val userId: String,
    val backupTimestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0",
    val devices: List<Device>,
    val macros: List<Macro>,
    val profiles: List<FamilyProfile>
)
