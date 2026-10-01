package com.smartmate.ai.data.repository

import com.smartmate.ai.data.local.dao.ChatDao
import com.smartmate.ai.data.local.entity.ChatEntity
import com.smartmate.ai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
    fun observeChats(): Flow<List<ChatEntity>> = chatDao.observeAll()
    fun observeMessages(chatId: String): Flow<List<MessageEntity>> = chatDao.observeMessages(chatId)

    suspend fun createChat(title: String = "New Chat"): ChatEntity {
        val chat = ChatEntity(title = title)
        chatDao.upsert(chat)
        return chat
    }

    suspend fun renameChat(chat: ChatEntity, newTitle: String) =
        chatDao.upsert(chat.copy(title = newTitle, updatedAtEpochMillis = System.currentTimeMillis()))

    suspend fun deleteChat(chat: ChatEntity) {
        chatDao.deleteMessagesForChat(chat.id)
        chatDao.delete(chat)
    }

    suspend fun addMessage(chatId: String, text: String, isFromUser: Boolean) {
        chatDao.insertMessage(MessageEntity(chatId = chatId, isFromUser = isFromUser, text = text))
        chatDao.touchUpdatedAt(chatId, System.currentTimeMillis())
    }

    suspend fun touchChat(chat: ChatEntity) =
        chatDao.upsert(chat.copy(updatedAtEpochMillis = System.currentTimeMillis()))

    suspend fun clearAllHistory() {
        chatDao.deleteAllMessages()
        chatDao.deleteAllChats()
    }
}
