package com.smartmate.ai.ui.screens.tasks

import com.smartmate.ai.data.local.entity.TaskEntity
import com.smartmate.ai.domain.Category
import com.smartmate.ai.domain.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class TasksUiStateTest {

    private fun dueInDays(days: Int): Long =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }.timeInMillis

    private fun task(
        title: String,
        dueAt: Long? = null,
        isCompleted: Boolean = false
    ) = TaskEntity(
        title = title,
        dueAtEpochMillis = dueAt,
        priority = Priority.MEDIUM,
        category = Category.PERSONAL,
        isCompleted = isCompleted
    )

    @Test
    fun `today tab includes undated and due-today tasks but excludes completed and future tasks`() {
        val undated = task("Undated")
        val dueToday = task("Due today", dueAt = System.currentTimeMillis())
        val dueTomorrow = task("Due tomorrow", dueAt = dueInDays(1))
        val completedToday = task("Completed today", dueAt = System.currentTimeMillis(), isCompleted = true)

        val state = TasksUiState(
            selectedTab = TaskTab.TODAY,
            tasks = listOf(undated, dueToday, dueTomorrow, completedToday)
        )

        val visibleTitles = state.visibleTasks.map { it.title }
        assertEquals(listOf("Undated", "Due today"), visibleTitles)
    }

    @Test
    fun `upcoming tab only includes incomplete tasks due after today`() {
        val dueToday = task("Due today", dueAt = System.currentTimeMillis())
        val dueTomorrow = task("Due tomorrow", dueAt = dueInDays(1))
        val dueNextWeek = task("Due next week", dueAt = dueInDays(7))
        val completedFuture = task("Completed future", dueAt = dueInDays(2), isCompleted = true)

        val state = TasksUiState(
            selectedTab = TaskTab.UPCOMING,
            tasks = listOf(dueToday, dueTomorrow, dueNextWeek, completedFuture)
        )

        val visibleTitles = state.visibleTasks.map { it.title }
        assertEquals(listOf("Due tomorrow", "Due next week"), visibleTitles)
    }

    @Test
    fun `completed tab only includes completed tasks regardless of due date`() {
        val completed = task("Completed", isCompleted = true)
        val incomplete = task("Incomplete")

        val state = TasksUiState(
            selectedTab = TaskTab.COMPLETED,
            tasks = listOf(completed, incomplete)
        )

        assertEquals(listOf("Completed"), state.visibleTasks.map { it.title })
        assertTrue(state.visibleTasks.all { it.isCompleted })
    }
}
