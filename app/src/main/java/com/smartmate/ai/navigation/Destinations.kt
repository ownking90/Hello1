package com.smartmate.ai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.smartmate.ai.R

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Chat : Destination("chat")
    data object Tasks : Destination("tasks")
    data object Notes : Destination("notes")
    data object Profile : Destination("profile")
    data object Reminders : Destination("reminders")
    data object Planner : Destination("planner")
    data object AiTools : Destination("ai_tools")
    data object Settings : Destination("settings")
    data object Login : Destination("login")
    data object SignUp : Destination("sign_up")
}

data class BottomNavItem(val destination: Destination, @StringRes val labelRes: Int, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Destination.Home, R.string.nav_home, Icons.Filled.Home),
    BottomNavItem(Destination.Chat, R.string.nav_chat, Icons.Filled.Chat),
    BottomNavItem(Destination.Tasks, R.string.nav_tasks, Icons.Filled.CheckCircle),
    BottomNavItem(Destination.Notes, R.string.nav_notes, Icons.Filled.Note),
    BottomNavItem(Destination.Profile, R.string.nav_profile, Icons.Filled.Person)
)
