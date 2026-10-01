package com.smartmate.ai.ui.screens.aitools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartmate.ai.R
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.domain.AiToolType
import com.smartmate.ai.ui.common.SectionCard

private fun AiToolType.labelRes(): Int = when (this) {
    AiToolType.SUMMARIZER -> R.string.tool_summarizer
    AiToolType.GRAMMAR_FIXER -> R.string.tool_grammar
    AiToolType.EMAIL_WRITER -> R.string.tool_email
    AiToolType.CAPTION_GENERATOR -> R.string.tool_caption
    AiToolType.STUDY_NOTES -> R.string.tool_study_notes
    AiToolType.QUESTION_ANSWER -> R.string.tool_qna
    AiToolType.TRANSLATOR -> R.string.tool_translate
}

@Composable
fun AiToolsScreen(container: AppContainer) {
    val viewModel: AiToolsViewModel = viewModel(factory = AiToolsViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text(stringResource(R.string.ai_tools), style = MaterialTheme.typography.headlineMedium) }

        item {
            androidx.compose.foundation.lazy.LazyRow {
                items(AiToolType.entries) { tool ->
                    FilterChip(
                        modifier = Modifier.padding(4.dp),
                        selected = state.selectedTool == tool,
                        onClick = { viewModel.selectTool(tool) },
                        label = { Text(stringResource(tool.labelRes())) }
                    )
                }
            }
        }

        item {
            SectionCard {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.chat_message_hint)) }
                )
                Button(onClick = viewModel::run) { Text(stringResource(state.selectedTool.labelRes())) }
                if (state.isRunning) {
                    CircularProgressIndicator()
                } else {
                    state.output?.let { Text(it) }
                }
            }
        }
    }
}
