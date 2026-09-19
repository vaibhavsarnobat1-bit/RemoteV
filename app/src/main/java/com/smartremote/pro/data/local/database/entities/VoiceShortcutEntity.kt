package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_shortcuts")
data class VoiceShortcutEntity(
    @PrimaryKey
    val id: String,
    val triggerPhrase: String,
    val language: String,
    val macroId: String?,
    val targetDeviceId: String?,
    val commandName: String?
)
