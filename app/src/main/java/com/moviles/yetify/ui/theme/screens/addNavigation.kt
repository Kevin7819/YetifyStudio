package com.moviles.yetify.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.yetify.datastore.UserPreferences

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onActivitiesClick = { /* navigate to activities */ },
                onProgressClick = { /* navigate to activities */ },
                onTasksClick = { navController.navigate("mainMenu") },
                onReadingsClick = {  /* navigate to readings */ },
                onLogout = { /* logout logic */ },
                userPreferences = UserPreferences(LocalContext.current)
            )
        }
        composable("mainMenu") { MainMenuScreen(navController) }
        composable("taskList") { TaskListScreen(navController) }
        composable("addTask") { AddTaskScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
    }
}