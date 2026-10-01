package com.smartmate.ai.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.local.entity.ChatEntity
import com.smartmate.ai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(
    val chats: List<ChatEntity> = emptyList(),
    val selectedChatId: String? = null,
    val messages: List<MessageEntity> = emptyList(),
    val draft: String = "",
    val isSending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(private val container: AppContainer) : ViewModel() {

    private val _selectedChatId = MutableStateFlow<String?>(null)
    private val _draft = MutableStateFlow("")
    private val _isSending = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val chatsFlow = container.chatRepository.observeChats()

    private val effectiveChatIdFlow = combine(chatsFlow, _selectedChatId) { chats, selected ->
        selected ?: chats.firstOrNull()?.id
    }

    private val messagesFlow = effectiveChatIdFlow.flatMapLatest { chatId ->
        chatId?.let { container.chatRepository.observeMessages(it) } ?: flowOf(emptyList())
    }

    private val chatAndMessagesFlow = combine(chatsFlow, effectiveChatIdFlow, messagesFlow) { chats, chatId, messages ->
        Triple(chats, chatId, messages)
    }

    val uiState: StateFlow<ChatUiState> = combine(
        chatAndMessagesFlow,
        _draft,
        _isSending,
        _error
    ) { chatAndMessages, draft, sending, error ->
        val (chats, chatId, messages) = chatAndMessages
        ChatUiState(chats, chatId, messages, draft, sending, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState())

    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
    }

    fun onDraftChange(text: String) {
        _draft.value = text
    }

    fun startNewChat() {
        viewModelScope.launch {
            val chat = container.chatRepository.createChat()
            _selectedChatId.value = chat.id
        }
    }

    fun renameChat(chat: ChatEntity, newTitle: String) {
        viewModelScope.launch { container.chatRepository.renameChat(chat, newTitle) }
    }

    fun deleteChat(chat: ChatEntity) {
        viewModelScope.launch {
            container.chatRepository.deleteChat(chat)
            if (_selectedChatId.value == chat.id) _selectedChatId.value = null
        }
    }

    fun sendMessage(prefill: String = "") {
        val text = (prefill.ifBlank { _draft.value }).trim()
        if (text.isBlank()) return
        _draft.value = ""
        viewModelScope.launch {
            try {
                _isSending.value = true
                _error.value = null
                val existingChatId = uiState.value.selectedChatId
                val targetChatId = existingChatId ?: container.chatRepository.createChat(
                    title = text.take(30)
                ).also { _selectedChatId.value = it.id }.id

                container.chatRepository.addMessage(targetChatId, text, isFromUser = true)
                val history = uiState.value.messages.map { it.text }
                val reply = container.aiService.chatReply(history, text)
                container.chatRepository.addMessage(targetChatId, reply, isFromUser = false)
            } catch (t: Throwable) {
                _error.value = "Couldn't get a reply. Please try again."
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(container) as T
        }
    }
}
