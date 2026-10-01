package com.smartmate.ai.ui.screens.aitools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.domain.AiToolType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiToolsUiState(
    val selectedTool: AiToolType = AiToolType.SUMMARIZER,
    val input: String = "",
    val output: String? = null,
    val isRunning: Boolean = false
)

class AiToolsViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(AiToolsUiState())
    val uiState: StateFlow<AiToolsUiState> = _uiState.asStateFlow()

    fun selectTool(tool: AiToolType) {
        _uiState.value = _uiState.value.copy(selectedTool = tool, output = null)
    }

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(input = text)
    }

    fun run() {
        val state = _uiState.value
        if (state.input.isBlank()) return
        viewModelScope.launch {
            _uiState.value = state.copy(isRunning = true)
            val result = container.aiService.runTool(state.selectedTool, state.input)
            _uiState.value = _uiState.value.copy(isRunning = false, output = result)
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AiToolsViewModel(container) as T
        }
    }
}
