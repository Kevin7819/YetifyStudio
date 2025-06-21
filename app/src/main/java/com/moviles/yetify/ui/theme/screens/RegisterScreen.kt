package com.moviles.yetify.ui.theme.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun RegisterScreen(
    onRegister: (userName: String, email: String, password: String, birthday: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val maxCalendarDate = Calendar.getInstance().apply { add(Calendar.YEAR, -5) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
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
            .background(Color(0xFF4EB1CB))
    ) {
        // Fondo blanco superior tipo tarjeta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        // Formulario en tarjeta azul clara redondeada
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F6FB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Crear cuenta",
                        fontSize = 32.sp,
                        color = Color(0xFF4EB1CB),
                        fontWeight = FontWeight.Bold
                    )

                    // Nombre de usuario
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    )

                    // Correo electrónico
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    )

                    // Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    )

                    // Confirmar contraseña
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    )

                    // Fecha de nacimiento
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = birthday,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Fecha de nacimiento", color = Color(0xFF4EB1CB)) },
                            trailingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha",  tint = Color(0xFF4EB1CB))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
                            shape = RoundedCornerShape(50),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4EB1CB),
                                unfocusedBorderColor = Color(0xFF4EB1CB),
                                disabledBorderColor = Color(0xFF4EB1CB),
                                focusedLabelColor = Color(0xFF4EB1CB),
                                unfocusedLabelColor = Color(0xFF4EB1CB),
                                cursorColor = Color(0xFF4EB1CB)
                            )
                        )
                    }

                    // Mostrar errores
                    if (showError) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4EB1CB)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("CREAR CUENTA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    // Texto de navegación
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onBackToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("¿YA TIENES UNA CUENTA? ")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF4EB1CB),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("INICIA SESIÓN")
                                }
                            },
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}