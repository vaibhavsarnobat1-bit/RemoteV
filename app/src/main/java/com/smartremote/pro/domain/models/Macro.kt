package com.smartremote.pro.domain.models

data class MacroStep(
    val id: String,
    val macroId: String,
    val stepOrder: Int,
    val targetDeviceId: String,
    val commandName: String,
    val delayAfterMs: Long = 300L,
    val customValue: String? = null // e.g., channel number or temp setting
)

data class Macro(
    val id: String,
    val name: String,
    val description: String,
    val icon: String = "ic_macro_default",
    val isSystemPrebuilt: Boolean = false,
    val steps: List<MacroStep> = emptyList(),
    val triggerTagNfc: String? = null,
    val voiceTrigger: String? = null,
    val isEnabled: Boolean = true
)
