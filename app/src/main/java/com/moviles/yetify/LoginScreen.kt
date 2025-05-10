package com.moviles.yetify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(loginResult) {
        if (loginResult is AuthViewModel.LoginResult.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Botón Volver
        TextButton(onClick = { /* TODO: Navegar atrás */ }) {
            Text("Volver", color = Color(0xFF38B6FF))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Título
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar", color = Color(0xFF38B6FF), style = MaterialTheme.typography.headlineLarge)
            Text("Sesión", color = Color(0xFF38B6FF), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Ingresa tus credenciales para acceder a tu cuenta",
                color = Color(0xFF2D0C17)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Área azul celeste
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF77BFD7), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "¿Olvidaste tu contraseña?",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (loginResult is AuthViewModel.LoginResult.Error) {
                    Text(
                        text = (loginResult as AuthViewModel.LoginResult.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Button(
                    onClick = { viewModel.login(userName, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = userName.isNotBlank() && password.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("INICIAR SESIÓN", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Área inferior con curva blanca
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¿NO TIENES UNA CUENTA?", color = Color.Black)
                    TextButton(onClick = { /* TODO: Navegar a registro */ }) {
                        Text("REGÍSTRATE", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
