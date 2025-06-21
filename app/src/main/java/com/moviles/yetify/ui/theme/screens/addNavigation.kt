package com.moviles.yetify.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    // Navigation controller that remembers state between layouts
    val navController = rememberNavController()

    //NavHost contains all the routes (screens) available in the app
    NavHost(
        navController = navController,
        startDestination = "mainMenu" // initial screen
    ) {
        //available routes
        composable("mainMenu") { MainMenuScreen(navController) }
        composable("taskList") { TaskListScreen(navController) }
        composable("addTask") { AddTaskScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
    }
}