package com.smartremote.pro.domain.repository

import com.smartremote.pro.domain.models.Macro
import kotlinx.coroutines.flow.Flow

interface MacroRepository {
    fun getAllMacros(): Flow<List<Macro>>
    suspend fun getMacroById(id: String): Macro?
    suspend fun getMacroByVoiceTrigger(phrase: String): Macro?
    suspend fun getMacroByNfcTag(tagId: String): Macro?
    suspend fun saveMacro(macro: Macro)
    suspend fun deleteMacro(id: String)
    suspend fun initializeDefaultMacros()
}
