package com.smartmate.ai.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.ui.screens.aitools.AiToolsScreen
import com.smartmate.ai.ui.screens.chat.ChatScreen
import com.smartmate.ai.ui.screens.home.HomeScreen
import com.smartmate.ai.ui.screens.notes.NotesScreen
import com.smartmate.ai.ui.screens.planner.PlannerScreen
import com.smartmate.ai.ui.screens.profile.ProfileScreen
import com.smartmate.ai.ui.screens.reminders.RemindersScreen
import com.smartmate.ai.ui.screens.settings.SettingsScreen
import com.smartmate.ai.ui.screens.tasks.TasksScreen

@Composable
fun SmartMateNavHost(container: AppContainer) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    container = container,
                    onOpenChat = { navController.navigate(Destination.Chat.route) },
                    onOpenTasks = { navController.navigate(Destination.Tasks.route) },
                    onOpenReminders = { navController.navigate(Destination.Reminders.route) },
                    onOpenNotes = { navController.navigate(Destination.Notes.route) },
                    onOpenPlanner = { navController.navigate(Destination.Planner.route) },
                    onOpenAiTools = { navController.navigate(Destination.AiTools.route) }
                )
            }
            composable(Destination.Chat.route) { ChatScreen(container = container) }
            composable(Destination.Tasks.route) { TasksScreen(container = container) }
            composable(Destination.Notes.route) { NotesScreen(container = container) }
            composable(Destination.Reminders.route) { RemindersScreen(container = container) }
            composable(Destination.Planner.route) { PlannerScreen(container = container) }
            composable(Destination.AiTools.route) { AiToolsScreen(container = container) }
            composable(Destination.Settings.route) { SettingsScreen(container = container) }
            composable(Destination.Profile.route) {
                ProfileScreen(container = container, onOpenSettings = { navController.navigate(Destination.Settings.route) })
            }
        }
    }
}
