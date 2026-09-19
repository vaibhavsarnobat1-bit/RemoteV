package com.smartremote.pro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.domain.models.MacroStep
import com.smartremote.pro.domain.repository.MacroRepository
import com.smartremote.pro.domain.usecases.ExecuteMacroUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class MacroUiState(
    val macros: List<Macro> = emptyList(),
    val executingMacroId: String? = null,
    val currentStepIndex: Int = 0,
    val totalSteps: Int = 0,
    val isExecuting: Boolean = false
)

class MacroViewModel(
    private val macroRepository: MacroRepository,
    private val executeMacroUseCase: ExecuteMacroUseCase
) : ViewModel() {

    private val _executingMacroId = MutableStateFlow<String?>(null)
    private val _currentStep = MutableStateFlow(0)
    private val _totalSteps = MutableStateFlow(0)
    private val _isExecuting = MutableStateFlow(false)

    val uiState: StateFlow<MacroUiState> = combine(
        macroRepository.getAllMacros(),
        _executingMacroId,
        _currentStep,
        _totalSteps,
        _isExecuting
    ) { macros, activeId, step, total, executing ->
        MacroUiState(
            macros = macros,
            executingMacroId = activeId,
            currentStepIndex = step,
            totalSteps = total,
            isExecuting = executing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MacroUiState()
    )

    fun runMacro(macro: Macro) {
        viewModelScope.launch {
            _executingMacroId.value = macro.id
            _isExecuting.value = true
            _totalSteps.value = macro.steps.size

            executeMacroUseCase(macro) { current, total ->
                _currentStep.value = current
                _totalSteps.value = total
            }

            _isExecuting.value = false
            _executingMacroId.value = null
            _currentStep.value = 0
        }
    }

    fun createCustomMacro(name: String, description: String, steps: List<MacroStep>) {
        viewModelScope.launch {
            val macroId = "macro_${UUID.randomUUID()}"
            val newMacro = Macro(
                id = macroId,
                name = name,
                description = description,
                icon = "ic_macro_custom",
                isSystemPrebuilt = false,
                steps = steps.mapIndexed { idx, s -> s.copy(macroId = macroId, stepOrder = idx + 1) }
            )
            macroRepository.saveMacro(newMacro)
        }
    }

    fun deleteMacro(id: String) {
        viewModelScope.launch {
            macroRepository.deleteMacro(id)
        }
    }
}
