package com.smartmate.ai.data.repository

import com.smartmate.ai.data.local.dao.NoteDao
import com.smartmate.ai.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun observeNotes(): Flow<List<NoteEntity>> = noteDao.observeAll()

    suspend fun saveNote(note: NoteEntity) = noteDao.upsert(note.copy(updatedAtEpochMillis = System.currentTimeMillis()))

    suspend fun togglePin(note: NoteEntity) = noteDao.upsert(note.copy(isPinned = !note.isPinned))

    suspend fun deleteNote(note: NoteEntity) = noteDao.delete(note)
}
