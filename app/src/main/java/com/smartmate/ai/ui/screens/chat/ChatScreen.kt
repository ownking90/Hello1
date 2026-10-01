package com.smartmate.ai.ui.screens.chat

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartmate.ai.R
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.ui.common.EmptyState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(container: AppContainer, initialDraft: String = "") {
    val viewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(initialDraft) {
        if (initialDraft.isNotBlank()) viewModel.sendMessage(initialDraft)
    }

    val tts = remember {
        TextToSpeech(context) { }
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) viewModel.onDraftChange(spoken)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_chat)) },
                actions = {
                    IconButton(onClick = { viewModel.startNewChat() }) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_chat))
                    }
                }
            )
        }
    ) { padding ->
        if (state.chats.isEmpty() && state.messages.isEmpty()) {
            EmptyState(stringResource(R.string.empty_state_chats), Modifier.padding(padding))
            return@Scaffold
        }

        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).padding(8.dp)) {
                    items(state.messages) { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            colors = androidx.compose.material3.CardDefaults.cardColors()
                        ) {
                            Text(
                                text = message.text,
                                modifier = Modifier.padding(12.dp),
                                textAlign = if (message.isFromUser) androidx.compose.ui.text.style.TextAlign.End
                                else androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                        if (!message.isFromUser) {
                            LaunchedEffect(message.id) {
                                runCatching { tts.setLanguage(Locale.getDefault()); tts.speak(message.text, TextToSpeech.QUEUE_ADD, null, message.id) }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.draft,
                        onValueChange = viewModel::onDraftChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.chat_message_hint)) }
                    )
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        }
                        runCatching { voiceLauncher.launch(intent) }
                    }) {
                        Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.mic_input))
                    }
                    IconButton(onClick = { viewModel.sendMessage() }) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}
