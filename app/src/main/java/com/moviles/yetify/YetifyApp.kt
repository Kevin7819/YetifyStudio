package com.moviles.yetify

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.yetify.ui.theme.screens.AppNavigation
import com.moviles.yetify.ui.theme.screens.WelcomeScreen

import com.moviles.yetify.viewmodel.AuthViewModel
import androidx.compose.runtime.derivedStateOf

/**
 * The root composable of the app.
 * Manages:
 * - Authentication state (logged in or not)
 * - Navigation between Login and Main App
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun YetifyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome" // Cambiado de "login" a "welcome"
    ) {
        // Pantalla de bienvenida
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onCreateAccountClick = { /* Navegar a pantalla de registro */ }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            AppNavigation()
        }
    }
}