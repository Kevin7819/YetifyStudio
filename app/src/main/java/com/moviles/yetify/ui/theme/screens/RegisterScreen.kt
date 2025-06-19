package com.moviles.yetify.ui.theme.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun RegisterScreen(
    // Callback triggered when the user successfully fills the form and presses "Register"
    onRegister: (userName: String, email: String, password: String, birthday: String) -> Unit,
    // Callback to navigate back to the login screen
    onBackToLogin: () -> Unit
) {
    // State for user input fields
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    // Error state and message
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Context and calendar setup
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Set max date to ensure the user is at least 5 years old
    val maxCalendarDate = Calendar.getInstance().apply {
        add(Calendar.YEAR, -5)
    }

    // State to control when the DatePickerDialog is shown
    var showDatePicker by remember { mutableStateOf(false) }

    // Show the DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                // Validate age (at least 5 years)
                if (selectedDate.after(maxCalendarDate)) {
                    showError = true
                    errorMessage = "Debes tener al menos 5 años"
                } else {
                    birthday = "%04d-%02d-%02d".format(year, month + 1, day)
                    showError = false
                }
                showDatePicker = false
            },
            maxCalendarDate.get(Calendar.YEAR),
            maxCalendarDate.get(Calendar.MONTH),
            maxCalendarDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = maxCalendarDate.timeInMillis
        }.show()
    }

    // Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear cuenta", fontSize = 32.sp, color = Color(0xFF4EB1CB))

            // Nombre de usuario
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            // Correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            //  Campo de fecha de nacimiento envuelto en Box clickable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = birthday,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false, // <- evita interacción directa y permite que el Box capture el clic
                    label = { Text("Fecha de nacimiento") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Mostrar errores
            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            // Botón de registro
            Button(
                onClick = {
                    if (userName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || birthday.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios"
                        showError = true
                    } else if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        showError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Correo electrónico inválido"
                        showError = true
                    } else {
                        showError = false
                        onRegister(userName, email, password, birthday)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4EB1CB))
            ) {
                Text("Registrarse", color = Color.White)
            }

            // Botón para volver al login
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBackToLogin) {
                Text("¿Ya tienes una cuenta? Inicia sesión", color = Color(0xFF4EB1CB))
            }
        }
    }
}
