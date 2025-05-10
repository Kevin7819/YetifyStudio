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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo blanco superior
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botón "Volver"
            TextButton(
                onClick = { /* Acción de volver */ },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text("◀ Volver", color = Color(0xFF38B6FF))
            }

            // Título
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Iniciar",
                    color = Color(0xFF38B6FF),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "Sesión",
                    color = Color(0xFF38B6FF),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ingresa tus credenciales para acceder a tu cuenta",
                    color = Color(0xFF2D0C17),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Parte azul con inputs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Color(0xFF77BFD7),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Correo electrónico", color = Color.White) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Correo", tint = Color.White)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLeadingIconColor = Color.White,
                            unfocusedLeadingIconColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Contraseña",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Contraseña", tint = Color.White)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLeadingIconColor = Color.White,
                            unfocusedLeadingIconColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )


                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.White,
                        fontSize = 16.sp
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
                            .height(60.dp),
                        enabled = userName.isNotBlank() && password.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                "INICIAR SESIÓN",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿NO TIENES UNA CUENTA?", color = Color.Black)
                TextButton(onClick = { /* Acción de registro */ }) {
                    Text(
                        "REGÍSTRATE",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

        }
    }
}
