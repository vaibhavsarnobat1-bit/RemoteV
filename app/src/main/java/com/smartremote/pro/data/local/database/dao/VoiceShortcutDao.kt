package com.smartremote.pro.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartremote.pro.data.local.database.entities.VoiceShortcutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceShortcutDao {

    @Query("SELECT * FROM voice_shortcuts")
    fun getAllShortcuts(): Flow<List<VoiceShortcutEntity>>

    @Query("SELECT * FROM voice_shortcuts WHERE triggerPhrase = :phrase LIMIT 1")
    suspend fun getShortcutByPhrase(phrase: String): VoiceShortcutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: VoiceShortcutEntity)

    @Query("DELETE FROM voice_shortcuts WHERE id = :id")
    suspend fun deleteShortcut(id: String)
}
