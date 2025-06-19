package com.moviles.yetify

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
import com.moviles.yetify.ui.theme.screens.*
import com.moviles.yetify.viewmodel.AuthViewModel

/**
 * Root composable for the Yetify application.
 * Sets up the navigation flow between screens
 * and connects them with the AuthViewModel logic.
 */
@Composable
fun YetifyApp() {

    // NavController instance to manage navigation between screens
    val navController = rememberNavController()

    // Instance of the authentication ViewModel
    val authViewModel: AuthViewModel = viewModel()

    // Navigation host defining all available routes/screens
    NavHost(
        navController = navController,
        startDestination = "welcome" // Initial screen when the app starts
    ) {

        // -------------------- Welcome Screen --------------------
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },         // Navigate to login screen
                onCreateAccountClick = { navController.navigate("register") } // Navigate to register screen
            )
        }

        // -------------------- Login Screen --------------------
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    // Navigate to main screen and remove welcome from back stack
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password") // Navigate to forgot password screen
                }
            )
        }

        // -------------------- Register Screen --------------------
        composable("register") {
            RegisterScreen(
                onRegister = { userName, email, password, birthday ->
                    // Call ViewModel to register the user
                    authViewModel.register(userName, email, password, birthday)
                },
                onBackToLogin = {
                    // Go back to the previous screen (likely login)
                    navController.popBackStack()
                }
            )

            // Observe register result from ViewModel
            val registerResult by authViewModel.registerResult.collectAsState()

            // React to the result of registration
            LaunchedEffect(registerResult) {
                when (registerResult) {
                    "success" -> {
                        // Registration succeeded → navigate to login
                        navController.navigate("login") {
                            popUpTo("welcome") { inclusive = true }
                        }
                        authViewModel.clearRegisterResult()
                    }
                    is String -> if (!registerResult.isNullOrBlank()) {
                        // Print registration error to console
                        println("❌ Registration error: $registerResult")
                        authViewModel.clearRegisterResult()
                    }
                }
            }
        }

        // -------------------- Forgot Password Screen --------------------
        composable("forgot_password") {
            ForgotPasswordScreen(
                onSendCodeClick = { email ->
                    // Call API to send code and navigate to verify screen
                    authViewModel.sendForgotPassword(email)
                    navController.navigate("verify_code/$email")
                },
                onBackClick = {
                    navController.popBackStack() // Navigate back to the previous screen
                }
            )
        }

        // -------------------- Verify Code and Reset Password Screen --------------------
        composable(
            route = "verify_code/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->

            // Get the email argument from navigation
            val email = requireNotNull(backStackEntry.arguments?.getString("email")) {
                "Email cannot be null in this screen"
            }

            // Observe the reset password result
            val resetResult by authViewModel.resetPasswordResult.collectAsState()

            VerifyCodeAndResetPasswordScreen(
                email = email,
                onResetPassword = { code, newPassword, confirmPassword ->
                    authViewModel.resetPassword(email, code, newPassword, confirmPassword)
                },
                onBackClick = { navController.popBackStack() }
            )

            // React to the result of password reset
            LaunchedEffect(resetResult) {
                when (resetResult) {
                    "success" -> {
                        // Password reset succeeded → go to login
                        navController.navigate("login") {
                            popUpTo("welcome") { inclusive = true }
                        }
                        authViewModel.clearResetPasswordState()
                    }
                    is String -> if (resetResult!!.isNotBlank()) {
                        // Print reset error to console
                        println("❌ Error resetting password: $resetResult")
                        authViewModel.clearResetPasswordState()
                    }
                }
            }
        }

        // -------------------- Main Home Screen --------------------
        composable("main") {
            HomeScreen(
                onActivitiesClick = { /* TODO: Navigate to activities screen */ },
                onProgressClick = { /* TODO: Navigate to progress screen */ },
                onTasksClick = { navController.navigate("tasks") }, // Navigate to tasks screen
                onReadingsClick = { /* TODO: Navigate to readings screen */ }
            )
        }

        // -------------------- Internal Task Navigation --------------------
        composable("tasks") {
            AppNavigation() // Load task-related internal navigation
        }
    }
}
