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
 * - Forgot Password (send code)
 * - Reset Password (using code)
 * - Logout
 *
 * It uses Retrofit for API requests and StateFlows to manage UI state.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // State for login result
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    // General loading flag
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Tracks if user is authenticated
    var isAuthenticated by mutableStateOf(false)
        private set

    // Holds current logged-in user's ID
    var userId: Int? = null

    // Shared preferences instance for storing user data
    private val prefs = UserPreferences(application.applicationContext)

    init {
        // Initialize Retrofit with the application context (for interceptor setup)
        RetrofitInstance.init(application)
    }

    /**
     * Represents the login result states for UI to observe.
     */
    sealed class LoginResult {
        object Idle : LoginResult()
        object Loading : LoginResult()
        data class Success(val response: LoginResponse) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    /**
     * Attempts to authenticate the user using given credentials.
     */
    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            _isLoading.value = true

            try {
                val response = RetrofitInstance.api.login(LoginRequest(userName, password))

                if (response.isSuccessful) {
                    response.body()?.let { apiResponse: LoginApiResponse ->
                        val user = apiResponse.user

                        userId = user.id
                        isAuthenticated = true

                        // Store user ID and token in preferences
                        prefs.saveUser(user.id, user.token)
                        _loginResult.value = LoginResult.Success(user)

                        // Log for debugging
                        Log.i("AuthViewModel", "Saved user id: ${prefs.userId.first()}")
                        Log.i("AuthViewModel", "Saved token: ${prefs.token.first()}")
                        Log.i("AuthViewModel", "Login success. User: $user")
                    } ?: run {
                        _loginResult.value = LoginResult.Error("Empty response body")
                        Log.e("AuthViewModel", "Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _loginResult.value = LoginResult.Error("Login failed: $errorBody")
                    Log.e("AuthViewModel", "Login failed: $errorBody")
                }

            } catch (e: HttpException) {
                val errorMsg = "HTTP Error: ${e.message()}, Body: ${e.response()?.errorBody()?.string()}"
                _loginResult.value = LoginResult.Error(errorMsg)
                Log.e("AuthViewModel", errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message ?: "Unknown error"}"
                _loginResult.value = LoginResult.Error(errorMsg)
                Log.e("AuthViewModel", errorMsg, e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -------------------- Forgot / Reset Password Logic --------------------

    private val _isSendingCode = MutableStateFlow(false)
    val isSendingCode: StateFlow<Boolean> get() = _isSendingCode

    private val _isResettingPassword = MutableStateFlow(false)
    val isResettingPassword: StateFlow<Boolean> get() = _isResettingPassword

    private val _forgotPasswordResult = MutableStateFlow<String?>(null)
    val forgotPasswordResult: StateFlow<String?> get() = _forgotPasswordResult

    private val _resetPasswordResult = MutableStateFlow<String?>(null)
    val resetPasswordResult: StateFlow<String?> get() = _resetPasswordResult

    /**
     * Sends a password reset code to the user's email.
     */
    fun sendForgotPassword(email: String) {
        viewModelScope.launch {
            _isSendingCode.value = true
            try {
                val response = RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _forgotPasswordResult.value = "success"
                } else {
                    _forgotPasswordResult.value = response.body()?.message ?: "Failed to send code"
                }

            } catch (e: Exception) {
                _forgotPasswordResult.value = "Error: ${e.message}"
            } finally {
                _isSendingCode.value = false
            }
        }
    }

    /**
     * Attempts to reset the password using a code sent to the user's email.
     * Sends a ResetPasswordRequest to the backend and updates UI state accordingly.
     */
    fun resetPassword(email: String, code: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            _isResettingPassword.value = true
            Log.i("AuthViewModel", "Starting password reset for email: $email")

            try {
                val request = ResetPasswordRequest(
                    email = email.trim(),
                    code = code.trim(),
                    newPassword = newPassword,
                    confirmPassword = confirmPassword
                )

                Log.i("AuthViewModel", "Sending resetPassword request: $request")
                val response = RetrofitInstance.api.resetPassword(request)

                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {
                    if (responseBody.isSuccess) {
                        _resetPasswordResult.value = "success"
                        Log.i("AuthViewModel", "Password reset successful for email: $email")
                    } else {
                        val message = responseBody.message ?: "Unknown server error"
                        _resetPasswordResult.value = message
                        Log.e("AuthViewModel", "Password reset failed (logical error): $message")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = errorBody ?: "Unexpected error while resetting password"
                    _resetPasswordResult.value = message
                    Log.e("AuthViewModel", "Password reset failed (HTTP error). Code: ${response.code()}, Body: $errorBody")
                }

            } catch (e: Exception) {
                val errorMsg = "Exception during password reset: ${e.message ?: "Unknown error"}"
                _resetPasswordResult.value = "Error: $errorMsg"
                Log.e("AuthViewModel", errorMsg, e)
            } finally {
                _isResettingPassword.value = false
                Log.i("AuthViewModel", "Finished resetPassword(). isResettingPassword=false")
            }
        }
    }

    /**
     * Clears the reset password result to prevent repeated navigation or message.
     */
    fun clearResetPasswordState() {
        _resetPasswordResult.value = null
    }

    /**
     * Logs out the current user and clears saved data.
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
