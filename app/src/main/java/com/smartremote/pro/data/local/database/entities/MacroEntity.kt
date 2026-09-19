package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.domain.models.MacroStep

@Entity(tableName = "macros")
data class MacroEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isSystemPrebuilt: Boolean,
    val triggerTagNfc: String?,
    val voiceTrigger: String?,
    val isEnabled: Boolean
) {
    fun toDomain(steps: List<MacroStep>): Macro {
        return Macro(
            id = id,
            name = name,
            description = description,
            icon = icon,
            isSystemPrebuilt = isSystemPrebuilt,
            steps = steps,
            triggerTagNfc = triggerTagNfc,
            voiceTrigger = voiceTrigger,
            isEnabled = isEnabled
        )
    }

    companion object {
        fun fromDomain(macro: Macro): MacroEntity {
            return MacroEntity(
                id = macro.id,
                name = macro.name,
                description = macro.description,
                icon = macro.icon,
                isSystemPrebuilt = macro.isSystemPrebuilt,
                triggerTagNfc = macro.triggerTagNfc,
                voiceTrigger = macro.voiceTrigger,
                isEnabled = macro.isEnabled
            )
        }
    }
}

@Entity(tableName = "macro_steps")
data class MacroStepEntity(
    @PrimaryKey
    val id: String,
    val macroId: String,
    val stepOrder: Int,
    val targetDeviceId: String,
    val commandName: String,
    val delayAfterMs: Long,
    val customValue: String?
) {
    fun toDomain(): MacroStep {
        return MacroStep(
            id = id,
            macroId = macroId,
            stepOrder = stepOrder,
            targetDeviceId = targetDeviceId,
            commandName = commandName,
            delayAfterMs = delayAfterMs,
            customValue = customValue
        )
    }

    companion object {
        fun fromDomain(step: MacroStep): MacroStepEntity {
            return MacroStepEntity(
                id = step.id,
                macroId = step.macroId,
                stepOrder = step.stepOrder,
                targetDeviceId = step.targetDeviceId,
                commandName = step.commandName,
                delayAfterMs = step.delayAfterMs,
                customValue = step.customValue
            )
        }
    }
}
