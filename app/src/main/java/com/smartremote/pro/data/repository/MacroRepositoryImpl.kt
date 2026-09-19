package com.smartremote.pro.data.repository

import com.smartremote.pro.data.local.database.dao.MacroDao
import com.smartremote.pro.data.local.database.entities.MacroEntity
import com.smartremote.pro.data.local.database.entities.MacroStepEntity
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.domain.models.MacroStep
import com.smartremote.pro.domain.repository.MacroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class MacroRepositoryImpl(
    private val macroDao: MacroDao
) : MacroRepository {

    override fun getAllMacros(): Flow<List<Macro>> {
        return macroDao.getAllMacros().map { list ->
            list.map { entity ->
                val steps = macroDao.getStepsForMacro(entity.id).map { it.toDomain() }
                entity.toDomain(steps)
            }
        }
    }

    override suspend fun getMacroById(id: String): Macro? {
        val entity = macroDao.getMacroById(id) ?: return null
        val steps = macroDao.getStepsForMacro(id).map { it.toDomain() }
        return entity.toDomain(steps)
    }

    override suspend fun getMacroByVoiceTrigger(phrase: String): Macro? {
        val entity = macroDao.getMacroByVoiceTrigger(phrase) ?: return null
        val steps = macroDao.getStepsForMacro(entity.id).map { it.toDomain() }
        return entity.toDomain(steps)
    }

    override suspend fun getMacroByNfcTag(tagId: String): Macro? {
        val entity = macroDao.getMacroByNfcTag(tagId) ?: return null
        val steps = macroDao.getStepsForMacro(entity.id).map { it.toDomain() }
        return entity.toDomain(steps)
    }

    override suspend fun saveMacro(macro: Macro) {
        val entity = MacroEntity.fromDomain(macro)
        val steps = macro.steps.map { MacroStepEntity.fromDomain(it) }
        macroDao.insertFullMacro(entity, steps)
    }

    override suspend fun deleteMacro(id: String) {
        macroDao.deleteMacro(id)
    }

    override suspend fun initializeDefaultMacros() {
        val movieModeId = "macro_movie_mode"
        val movieMode = Macro(
            id = movieModeId,
            name = "Movie Mode",
            description = "TV on + Netflix open + Volume 25 + AC 24°C",
            icon = "ic_movie",
            isSystemPrebuilt = true,
            voiceTrigger = "movie time",
            steps = listOf(
                MacroStep(UUID.randomUUID().toString(), movieModeId, 1, "default_tv", "power", 800L),
                MacroStep(UUID.randomUUID().toString(), movieModeId, 2, "default_tv", "netflix", 500L),
                MacroStep(UUID.randomUUID().toString(), movieModeId, 3, "default_tv", "volUp", 200L),
                MacroStep(UUID.randomUUID().toString(), movieModeId, 4, "default_ac", "power", 300L)
            )
        )

        val gamingModeId = "macro_gaming_mode"
        val gamingMode = Macro(
            id = gamingModeId,
            name = "Gaming Mode",
            description = "TV HDMI 1 + Soundbar On + Bass Boost",
            icon = "ic_gaming",
            isSystemPrebuilt = true,
            voiceTrigger = "gaming mode",
            steps = listOf(
                MacroStep(UUID.randomUUID().toString(), gamingModeId, 1, "default_tv", "power", 500L),
                MacroStep(UUID.randomUUID().toString(), gamingModeId, 2, "default_tv", "source", 300L),
                MacroStep(UUID.randomUUID().toString(), gamingModeId, 3, "default_soundbar", "power", 200L)
            )
        )

        val nightModeId = "macro_night_mode"
        val nightMode = Macro(
            id = nightModeId,
            name = "Night Mode",
            description = "All devices off + Night light",
            icon = "ic_night",
            isSystemPrebuilt = true,
            voiceTrigger = "sleep",
            steps = listOf(
                MacroStep(UUID.randomUUID().toString(), nightModeId, 1, "default_tv", "power", 300L),
                MacroStep(UUID.randomUUID().toString(), nightModeId, 2, "default_ac", "tempUp", 200L)
            )
        )

        saveMacro(movieMode)
        saveMacro(gamingMode)
        saveMacro(nightMode)
    }
}
