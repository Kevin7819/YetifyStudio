package com.moviles.yetify.ui.theme.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val triviaViewModel: TriviaViewModel = viewModel()

    NavHost(navController, startDestination = "activities") {
        composable("activities") {
            ActivitiesScreen(
                onTriviaClick = { navController.navigate("categories") },
                onAudiobooksClick = { navController.navigate("audiobooks") }
            )
        }
        composable("categories") {
            TriviaCategoriesScreen { category ->
                triviaViewModel.fetchQuestions(category)
                navController.navigate("triviaGame")
            }
        }
        composable("triviaGame") {
            val questions by triviaViewModel.questions.collectAsState()
            val loading by triviaViewModel.loading.collectAsState()
            if (loading) {
                androidx.compose.material3.CircularProgressIndicator()
            } else {
                TriviaGameScreen(
                    questions = questions,
                    onFinish = { navController.popBackStack("activities", false) }
                )
            }
        }
    }
}