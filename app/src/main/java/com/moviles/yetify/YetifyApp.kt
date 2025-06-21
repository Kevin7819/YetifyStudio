package com.moviles.yetify

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.yetify.models.Book
import com.moviles.yetify.ui.theme.screens.*
import com.moviles.yetify.viewmodel.AuthViewModel

/**
 * The root composable of the app.
 * Handles:
 * - Navigation flow
 * - Passing required data between screens (e.g., email)
 * - Connecting UI screens with ViewModel logic
 */
@Composable
fun YetifyApp() {

    val navController = rememberNavController()

    // Initialize AuthViewModel
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // Welcome Screen
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onCreateAccountClick = {
                    // Navegar a pantalla de registro si se implementa
                }
            )
        }

        // Login Screen
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )
        }

        // Forgot Password Screen
        composable("forgot_password") {
            ForgotPasswordScreen(
                onSendCodeClick = { email ->
                    authViewModel.sendForgotPassword(email)
                    navController.navigate("verify_code/$email")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Verify Code and Reset Password Screen
        composable(
            route = "verify_code/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->

            val email = requireNotNull(backStackEntry.arguments?.getString("email")) {
                "El email no puede ser nulo en esta pantalla"
            }

            val resetResult by authViewModel.resetPasswordResult.collectAsState()

            VerifyCodeAndResetPasswordScreen(
                email = email,
                onResetPassword = { code, newPassword, confirmPassword ->
                    authViewModel.resetPassword(email, code, newPassword, confirmPassword)
                },
                onBackClick = { navController.popBackStack() }
            )

            // Reaccionar al resultado del cambio de contraseña
            LaunchedEffect(resetResult) {
                when (resetResult) {
                    "success" -> {
                        navController.navigate("login") {
                            popUpTo("welcome") { inclusive = true }
                        }
                        authViewModel.clearResetPasswordState()
                    }
                    is String -> if (resetResult!!.isNotBlank()) {
                        println("Error al cambiar contraseña: $resetResult")
                        authViewModel.clearResetPasswordState()
                    }
                }
            }
        }

        // Main Home Screen
        composable("main") {
            HomeScreen(
                onActivitiesClick = { /* navController.navigate("activities") */ },
                onProgressClick = { /* navController.navigate("progress") */ },
                onTasksClick = { navController.navigate("tasks") },
                onReadingsClick = {  navController.navigate("readings")  }
            )
        }

        // Tasks navigation
        composable("tasks") {
            AppNavigation()
        }
        // book activities
        composable("readings") {
            ScreenListBooks(
                onClickBack = {
                    navController.navigate("main")
                },
                onClickBook = {book: Book ->
                    navController.navigate("detailScreen/${book.id}")
                }
            )
        }
        composable(
            route = "detailScreen/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId")
            bookId?.let {
                ScreenBookReader(
                    id = it,
                    onClickBack = { navController.navigate("readings") }
                )
            } ?: run {
                Text("Error: Book ID missing")
            }
        }
    }
}
