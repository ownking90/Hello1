package com.smartmate.ai.data.repository

import com.smartmate.ai.data.local.dao.TaskDao
import com.smartmate.ai.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun observeTasks(): Flow<List<TaskEntity>> = taskDao.observeAll()

    suspend fun saveTask(task: TaskEntity) = taskDao.upsert(task.copy(updatedAtEpochMillis = System.currentTimeMillis()))

    suspend fun setCompleted(task: TaskEntity, completed: Boolean) =
        taskDao.update(task.copy(isCompleted = completed, updatedAtEpochMillis = System.currentTimeMillis()))

    suspend fun deleteTask(task: TaskEntity) = taskDao.delete(task)
}
