package com.smartremote.pro.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartremote.pro.data.local.database.dao.ChannelDao
import com.smartremote.pro.data.local.database.dao.DeviceDao
import com.smartremote.pro.data.local.database.dao.MacroDao
import com.smartremote.pro.data.local.database.dao.ProfileDao
import com.smartremote.pro.data.local.database.dao.ScheduleDao
import com.smartremote.pro.data.local.database.dao.VoiceShortcutDao
import com.smartremote.pro.data.local.database.entities.ChannelEntity
import com.smartremote.pro.data.local.database.entities.DeviceEntity
import com.smartremote.pro.data.local.database.entities.MacroEntity
import com.smartremote.pro.data.local.database.entities.MacroStepEntity
import com.smartremote.pro.data.local.database.entities.ProfileEntity
import com.smartremote.pro.data.local.database.entities.ScheduleEntity
import com.smartremote.pro.data.local.database.entities.VoiceShortcutEntity

@Database(
    entities = [
        DeviceEntity::class,
        MacroEntity::class,
        MacroStepEntity::class,
        ScheduleEntity::class,
        ChannelEntity::class,
        VoiceShortcutEntity::class,
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao
    abstract fun macroDao(): MacroDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun channelDao(): ChannelDao
    abstract fun voiceShortcutDao(): VoiceShortcutDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_remote.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
