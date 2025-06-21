package com.moviles.yetify


import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.*

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.yetify.models.Book
import com.moviles.yetify.ui.theme.screens.*
import com.moviles.yetify.viewmodel.AuthViewModel
import com.moviles.yetify.viewmodel.BookViewModel
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


    //book view Model
    val bookviewmodel: BookViewModel = viewModel()


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
                },
                onCreateAccountClick = {
                    navController.navigate("register")
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

                onReadingsClick = {  navController.navigate("readings")  },
                onActivitiesClick = {navController.navigate("trivia_categories")},
                onProgressClick = { navController.navigate("progress") },
                onTasksClick = { navController.navigate("tasks") }, // Navigate to tasks screen
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
        composable("progress") { //
            ProgressScreen(navController)
        }

        //---------------------- Trivia navigation
        composable("trivia_categories") {
            TriviaCategoryScreen(navController)
        }
        composable(
            "trivia/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 17
            TriviaQuestionScreen(navController, categoryId)
        }
        // book activities
        composable("readings") {
            ScreenListBooks(
                onClickBack = {
                    navController.navigate("main")
                },
                onClickBook = {book: Book ->
                    navController.navigate("detailScreen/${book.id}")
                },
                bookviewmodel = bookviewmodel
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
                    onClickBack = { navController.navigate("readings") },
                    bookviewmodel = bookviewmodel,
                    onComplete = {book:Book?->
                        navController.navigate("readbookcomplete/${book?.id}")
                    }
                )
            } ?: run {
                navController.navigate("main")
                //Text("Error: Book ID missing")
            }
        }
        composable(route="readbookcomplete/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId")
            bookId?.let {
                Log.i("yetify","bookid ${bookId}")
                ScreenCompleteDialog(
                    onClickBack = {navController.navigate("readings")},
                    onClickHome = {navController.navigate("main")},
                    onClickStats = {navController.navigate("main")},
                    onClickReset = {navController.navigate("detailScreen/${bookId}")}
                )
            } ?: run {
                navController.navigate("main")
                //Text("Error: Book ID missing")
            }
        }
    }
}
