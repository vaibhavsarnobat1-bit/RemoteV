package com.smartremote.pro.domain.usecases

import com.smartremote.pro.core.macro.MacroExecutionEngine
import com.smartremote.pro.domain.models.Macro

class ExecuteMacroUseCase(
    private val macroExecutionEngine: MacroExecutionEngine
) {
    suspend operator fun invoke(macro: Macro, onStepProgress: ((stepIndex: Int, total: Int) -> Unit)? = null): Boolean {
        return macroExecutionEngine.execute(macro, onStepProgress)
    }
}
