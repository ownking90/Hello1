package com.smartmate.ai.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.smartmate.ai.ui.common.SectionCard

@Composable
fun PlannerScreen(container: AppContainer) {
    val viewModel: PlannerViewModel = viewModel(factory = PlannerViewModel.factory(container))
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text(stringResource(R.string.daily_planner), style = MaterialTheme.typography.headlineMedium) }

        item {
            SectionCard {
                Text(stringResource(R.string.morning_overview), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.planner_overview_summary, state.todaysTasks.size, state.todaysReminders.size, state.recentNotes.size))
            }
        }

        item {
            SectionCard {
                Text(stringResource(R.string.todays_tasks), style = MaterialTheme.typography.titleMedium)
                if (state.todaysTasks.isEmpty()) Text(stringResource(R.string.nothing_due_today))
                state.todaysTasks.forEach { Text("• ${it.title}") }
            }
        }

        item {
            SectionCard {
                Text(stringResource(R.string.upcoming_reminders), style = MaterialTheme.typography.titleMedium)
                if (state.todaysReminders.isEmpty()) Text(stringResource(R.string.no_reminders_today))
                state.todaysReminders.forEach { Text("• ${it.title}") }
            }
        }

        item {
            SectionCard {
                Column {
                    Text(stringResource(R.string.ai_daily_plan), style = MaterialTheme.typography.titleMedium)
                    Button(onClick = viewModel::generatePlan) { Text(stringResource(R.string.generate_ai_plan)) }
                    if (state.isGeneratingPlan) {
                        CircularProgressIndicator()
                    } else {
                        state.aiPlan?.let { Text(it) }
                    }
                }
            }
        }
    }
}
