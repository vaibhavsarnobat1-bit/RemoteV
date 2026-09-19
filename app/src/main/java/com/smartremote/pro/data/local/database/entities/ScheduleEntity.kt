package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey
    val id: String,
    val macroId: String,
    val title: String,
    val timeHour: Int,
    val timeMinute: Int,
    val repeatDaysOfWeek: String, // e.g. "MON,TUE,WED,THU,FRI"
    val isEnabled: Boolean = true
)
