package com.smartremote.pro.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smartremote.pro.data.local.database.entities.MacroEntity
import com.smartremote.pro.data.local.database.entities.MacroStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MacroDao {

    @Query("SELECT * FROM macros ORDER BY isSystemPrebuilt DESC, name ASC")
    fun getAllMacros(): Flow<List<MacroEntity>>

    @Query("SELECT * FROM macros WHERE id = :id LIMIT 1")
    suspend fun getMacroById(id: String): MacroEntity?

    @Query("SELECT * FROM macros WHERE voiceTrigger = :trigger LIMIT 1")
    suspend fun getMacroByVoiceTrigger(trigger: String): MacroEntity?

    @Query("SELECT * FROM macros WHERE triggerTagNfc = :nfcTag LIMIT 1")
    suspend fun getMacroByNfcTag(nfcTag: String): MacroEntity?

    @Query("SELECT * FROM macro_steps WHERE macroId = :macroId ORDER BY stepOrder ASC")
    suspend fun getStepsForMacro(macroId: String): List<MacroStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMacro(macro: MacroEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<MacroStepEntity>)

    @Query("DELETE FROM macro_steps WHERE macroId = :macroId")
    suspend fun deleteStepsForMacro(macroId: String)

    @Transaction
    suspend fun insertFullMacro(macro: MacroEntity, steps: List<MacroStepEntity>) {
        insertMacro(macro)
        deleteStepsForMacro(macro.id)
        insertSteps(steps)
    }

    @Query("DELETE FROM macros WHERE id = :id")
    suspend fun deleteMacro(id: String)
}
