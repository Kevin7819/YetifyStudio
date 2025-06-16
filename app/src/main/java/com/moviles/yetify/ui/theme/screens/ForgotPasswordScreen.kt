package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Composable screen that allows the user to input their email
 * to request a password reset code.
 *
 * After clicking the button, it calls onSendCodeClick(email)
 * so the parent composable can handle the logic (sending the code and navigation).
 *
 * @param onSendCodeClick Lambda to call when user submits the email.
 * @param onBackClick Lambda to call when user presses the back button.
 */
@Composable
fun ForgotPasswordScreen(
    onSendCodeClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // State variable to hold the email input
    var email by remember { mutableStateOf("") }
    // State variable to show loading while sending
    var isSending by remember { mutableStateOf(false) }
    // State variable to show any local error message
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            TextButton(
                onClick = { onBackClick() },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text("◀ Volver", color = Color(0xFF38B6FF))
            }

            // Titles
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Recuperar",
                    color = Color(0xFF38B6FF),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "Contraseña",
                    color = Color(0xFF38B6FF),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Ingresa tu correo y te enviaremos un código",
                    color = Color(0xFF2D0C17),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Blue box for the form
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
                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", color = Color.White) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Correo",
                                tint = Color.White
                            )
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

                    // Show error message if any
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Button to send code
                    Button(
                        onClick = {
                            // Reset error state and start sending
                            errorMessage = ""
                            isSending = true

                            // Call parent function to handle code sending and navigation
                            onSendCodeClick(email)

                            // Simulate finished sending after calling parent (optional)
                            isSending = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = email.isNotBlank() && !isSending,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                "ENVIAR CÓDIGO",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
