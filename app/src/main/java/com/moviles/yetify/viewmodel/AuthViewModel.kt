package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.models.LoginApiResponse
import com.moviles.yetify.models.LoginRequest
import com.moviles.yetify.models.LoginResponse
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel responsible for handling authentication logic and state.
 *
 * @param application The Android Application context
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var isAuthenticated by mutableStateOf(false)
        private set

    var userId: Int? = null

    sealed class LoginResult {
        object Idle : LoginResult()
        object Loading : LoginResult()
        data class Success(val response: LoginResponse) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

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
                        _loginResult.value = LoginResult.Success(user)

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

    fun logout() {
        isAuthenticated = false
        userId = null
        _loginResult.value = LoginResult.Idle
    }
}
