package com.moviles.yetify

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.yetify.viewmodel.AuthViewModel

/**
 * Composable function that displays the login screen and handles authentication flow.
 *
 * @param onLoginSuccess Callback invoked when login is successful, for navigation
 * @param viewModel The AuthViewModel that handles authentication logic
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // Local UI state
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ViewModel states
    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Handle successful login navigation
    LaunchedEffect(loginResult) {
        when (loginResult) {
            is AuthViewModel.LoginResult.Success -> {
                onLoginSuccess()
            }
            else -> {}
        }
    }

    // Main UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Username Field
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Email, "Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(24.dp))

        // Error Message
        when (loginResult) {
            is AuthViewModel.LoginResult.Error -> {
                Text(
                    text = (loginResult as AuthViewModel.LoginResult.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            else -> {}
        }

        // Login Button
        Button(
            onClick = { viewModel.login(userName, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = userName.isNotBlank() && password.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Sign In")
            }
        }
    }
}