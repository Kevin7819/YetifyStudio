package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.yetify.R
import com.moviles.yetify.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Navigate to main screen if login was successful
    LaunchedEffect(loginResult) {
        if (loginResult is AuthViewModel.LoginResult.Success) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Titles section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen Yeti
                Image(
                    painter = painterResource(id = R.drawable.yeti_surprised),
                    contentDescription = "Yeti sorprendido",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 8.dp)
                )
                Text(
                    "Bienvenido",
                    color = Color(0xFF4EB1CB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Text(
                    "Inicia sesión para continuar",
                    color = Color(0xFF4EB1CB),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    // Email input field
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Nombre de usuario", color = Color.White) },
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

                    // Password input field
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

                    // Clickable "Forgot password" text for navigation
                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show error message if login fails
                    if (loginResult is AuthViewModel.LoginResult.Error) {
                        Text(
                            text = (loginResult as AuthViewModel.LoginResult.Error).message,
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Login button
                    Button(
                        onClick = { viewModel.login(userName, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = userName.isNotBlank() && password.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4EB1CB)),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                "INICIAR SESIÓN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Botón para crear cuenta (registro)
                    Button(
                        onClick = onCreateAccountClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color(0xFF4EB1CB)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Crear cuenta",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4EB1CB),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}