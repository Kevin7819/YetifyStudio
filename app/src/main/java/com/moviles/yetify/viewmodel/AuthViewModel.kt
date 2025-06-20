package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.datastore.UserPreferences
import com.moviles.yetify.models.*
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel responsible for handling all authentication-related operations:
 * - Login
 * - Register
 * - Forgot Password (send code)
 * - Reset Password (using code)
 * - Logout
 *
 * Uses Retrofit for API requests and StateFlows to manage UI state reactively.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // MutableStateFlow to emit login result states (Idle, Loading, Success, Error)
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    // MutableStateFlow to indicate loading status (true when loading)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Compose state to track if user is authenticated
    var isAuthenticated by mutableStateOf(false)
        private set

    // Store the current logged-in user's ID
    var userId: Int? = null

    // Instance of UserPreferences for persistent user data storage (ID, token)
    private val prefs = UserPreferences(application.applicationContext)

    init {
        // Initialize Retrofit instance with the Application context
        RetrofitInstance.init(application)
    }

    /**
     * Sealed class to represent different login result states
     * for UI to observe and react accordingly.
     */
    sealed class LoginResult {
        object Idle : LoginResult()             // No login attempt yet
        object Loading : LoginResult()          // Login request in progress
        data class Success(val response: LoginResponse) : LoginResult()  // Login successful with user data
        data class Error(val message: String) : LoginResult()            // Login failed with error message
    }

    /**
     * Performs login by calling the backend API with username and password.
     * Updates UI state based on response success or failure.
     */
    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading  // Notify UI that login started
            _isLoading.value = true

            try {
                // API call to login endpoint
                val response = RetrofitInstance.api.login(LoginRequest(userName, password))

                if (response.isSuccessful) {
                    // Parse response body safely
                    Log.i("AuthViewModel", "Successful response")
                    response.body()?.let { apiResponse ->
                        val user = apiResponse.user

                        // Save user info in ViewModel state and preferences
                        userId = user.id
                        isAuthenticated = true

                        // Emit success state with user data
                        _loginResult.value = LoginResult.Success(user)
                        prefs.saveToken(user.token)

                        val userResponse = RetrofitInstance.api.getUserById(userId!!)

                        prefs.saveUser(userResponse)

                        // Debug logs for verification
                        Log.i("AuthViewModel", "Saved user id: ${prefs.userId.first()}")
                        Log.i("AuthViewModel", "Saved username: ${prefs.userName.first()}")
                        Log.i("AuthViewModel", "Saved token: ${prefs.token.first()}")
                        Log.i("AuthViewModel", "Login success. User: $user")
                    } ?: run {
                        // Response body was null
                        _loginResult.value = LoginResult.Error("Empty response body")
                        Log.e("AuthViewModel", "Empty response body")
                    }
                } else {
                    // Handle HTTP error responses
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _loginResult.value = LoginResult.Error("Login failed: $errorBody")
                    Log.e("AuthViewModel", "Login failed: $errorBody")
                }

            } catch (e: HttpException) {
                // HTTP protocol error
                val errorMsg = "HTTP Error: ${e.message()}, Body: ${e.response()?.errorBody()?.string()}"
                _loginResult.value = LoginResult.Error(errorMsg)
                Log.e("AuthViewModel", errorMsg)
            } catch (e: Exception) {
                // Other exceptions such as network failures
                val errorMsg = "Error: ${e.message ?: "Unknown error"}"
                _loginResult.value = LoginResult.Error(errorMsg)
                Log.e("AuthViewModel", errorMsg, e)
            } finally {
                // Loading finished regardless of success or failure
                _isLoading.value = false
            }
        }
    }

    // -------------------- REGISTER --------------------

    // StateFlow to hold result message or success state for registration operation
    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult: StateFlow<String?> get() = _registerResult

    /**
     * Calls the backend API to register a new user.
     * Emits success or error message to update UI accordingly.
     */
    fun register(userName: String, email: String, password: String, birthday: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = RegisterRequest(
                    userName = userName.trim(),
                    email = email.trim(),
                    password = password,
                    birthday = birthday // Format: "yyyy-MM-dd"
                )
                val response = RetrofitInstance.api.register(request)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _registerResult.value = "success"
                    Log.i("AuthViewModel", "Registration successful")
                } else {
                    // Obtener mensaje del servidor si lo hay
                    val serverMessage = response.body()?.message

                    // Intentar leer el cuerpo de error si el mensaje no está
                    val errorBody = response.errorBody()?.string()

                    val errorMsg = when {
                        !serverMessage.isNullOrBlank() -> "Registro fallido: $serverMessage"
                        !errorBody.isNullOrBlank() -> "Registro fallido. Detalles: $errorBody"
                        else -> "Registro fallido: Error desconocido (código ${response.code()})"
                    }

                    _registerResult.value = errorMsg
                    Log.e("AuthViewModel", errorMsg)
                }

            } catch (e: HttpException) {
                // Error HTTP (como 400, 500, etc.)
                val errorText = e.response()?.errorBody()?.string()
                val msg = "Error HTTP ${e.code()}: ${e.message()}. Detalles: ${errorText ?: "sin detalles"}"
                _registerResult.value = msg
                Log.e("AuthViewModel", msg, e)

            } catch (e: Exception) {
                // Errores de red, tiempo de espera, parsing, etc.
                val msg = "Error inesperado: ${e.message ?: "desconocido"}"
                _registerResult.value = msg
                Log.e("AuthViewModel", msg, e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Clears the current registration result to avoid displaying outdated messages.
     */
    fun clearRegisterResult() {
        _registerResult.value = null
    }

    // -------------------- FORGOT / RESET PASSWORD --------------------

    // StateFlow to indicate if "send forgot password code" API call is in progress
    private val _isSendingCode = MutableStateFlow(false)
    val isSendingCode: StateFlow<Boolean> get() = _isSendingCode

    // StateFlow to indicate if "reset password" API call is in progress
    private val _isResettingPassword = MutableStateFlow(false)
    val isResettingPassword: StateFlow<Boolean> get() = _isResettingPassword

    // StateFlow to hold result message or success state for forgot password operation
    private val _forgotPasswordResult = MutableStateFlow<String?>(null)
    val forgotPasswordResult: StateFlow<String?> get() = _forgotPasswordResult

    // StateFlow to hold result message or success state for reset password operation
    private val _resetPasswordResult = MutableStateFlow<String?>(null)
    val resetPasswordResult: StateFlow<String?> get() = _resetPasswordResult

    /**
     * Sends a password reset code to the given email.
     * Updates the UI state with success or failure message.
     */
    fun sendForgotPassword(email: String) {
        viewModelScope.launch {
            _isSendingCode.value = true  // Indicate sending process started
            try {
                val response = RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))
                _forgotPasswordResult.value =
                    if (response.isSuccessful && response.body()?.isSuccess == true) "success"
                    else response.body()?.message ?: "Failed to send code"
            } catch (e: Exception) {
                _forgotPasswordResult.value = "Error: ${e.message}"
            } finally {
                _isSendingCode.value = false
            }
        }
    }

    /**
     * Sends a request to reset the user's password using the provided code.
     * Emits a result message for the UI to react to.
     */
    fun resetPassword(email: String, code: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            _isResettingPassword.value = true
            try {
                val request = ResetPasswordRequest(email, code, newPassword, confirmPassword)
                val response = RetrofitInstance.api.resetPassword(request)

                val body = response.body()
                _resetPasswordResult.value =
                    if (response.isSuccessful && body?.isSuccess == true) "success"
                    else body?.message ?: "Unexpected error while resetting password"

            } catch (e: Exception) {
                _resetPasswordResult.value = "Error: ${e.message}"
            } finally {
                _isResettingPassword.value = false
            }
        }
    }

    /**
     * Clears the current reset password result to prevent outdated messages from persisting.
     */
    fun clearResetPasswordState() {
        _resetPasswordResult.value = null
    }

    // -------------------- LOGOUT --------------------

    /**
     * Logs the user out by clearing the stored credentials and resetting the state.
     */
    fun logout() {
        isAuthenticated = false
        userId = null
        _loginResult.value = LoginResult.Idle
        viewModelScope.launch {
            prefs.clearUser()
        }
    }
}
