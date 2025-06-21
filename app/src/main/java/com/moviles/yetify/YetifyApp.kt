package com.moviles.yetify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.yetify.ui.theme.screens.*
import com.moviles.yetify.viewmodel.AuthViewModel
import com.moviles.yetify.datastore.UserPreferences
import androidx.compose.ui.platform.LocalContext

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

    // Instance of UserPreferences to retrieve stored user data
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

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
                onActivitiesClick = {navController.navigate("activities")},
                onProgressClick = { /* TODO: Navigate to progress screen */ },
                onTasksClick = { navController.navigate("tasks") }, // Navigate to tasks screen
                onReadingsClick = { /* TODO: Navigate to readings screen */ },
                onLogout = {
                    // Clear back stack and return to welcome screen
                    navController.navigate("welcome") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                userPreferences = userPreferences
            )
        }

        // -------------------- Internal Task Navigation --------------------
        composable("tasks") {
            AppNavigation() // Load task-related internal navigation
        }
        
        // -------------------- Activities Screen --------------------
        composable("activities") {
            ActivitiesScreen(
                onTriviaClick = { navController.navigate("categories") },
                onAudiobooksClick = { navController.navigate("audiobooks") }
            )
        }
        composable("categories") {
        val triviaViewModel: TriviaViewModel = viewModel()
        val questions by triviaViewModel.questions.collectAsState()
        val loading by triviaViewModel.loading.collectAsState()
        var selectedCategory by remember { mutableStateOf<String?>(null) }
    
        // Cuando seleccionas una categoría, solo llamas a fetchQuestions
        TriviaCategoriesScreen { category ->
            selectedCategory = category
            triviaViewModel.fetchQuestions(category)
        }
    
        // Cuando las preguntas llegan, navega automáticamente
        LaunchedEffect(questions, loading) {
            if (!loading && questions.isNotEmpty() && selectedCategory != null) {
                navController.navigate("triviaGame")
            }
        }
    }
        // -------------------- Trivia Game Screen --------------------
        composable("triviaGame") {
            val triviaViewModel: TriviaViewModel = viewModel()
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
