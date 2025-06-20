package com.moviles.yetify.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.yetify.R
import com.moviles.yetify.datastore.UserPreferences

@Composable
fun HomeScreen(
    onActivitiesClick: () -> Unit,
    onProgressClick: () -> Unit,
    onTasksClick: () -> Unit,
    onReadingsClick: () -> Unit,
    onLogout: () -> Unit,
    userPreferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    // State variables to control visibility of dialogs
    var showProfileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Retrieve user data stored in DataStore (persisted values)
    val userName by userPreferences.userName.collectAsState(initial = "")
    val email by userPreferences.email.collectAsState(initial = "")
    val birthday by userPreferences.birthday.collectAsState(initial = "")
    val registrationDate by userPreferences.registrationDate.collectAsState(initial = "")

    // Root container of the HomeScreen
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // White background for a clean and simple look
    ) {
        // Main vertical layout
        Column(modifier = Modifier.fillMaxSize()) {

            // Top App Bar containing Profile and Settings buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB3E5FC)) // Light blue background for header
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile icon button on the left
                IconButton(onClick = { showProfileDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Perfil",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }

                // Settings icon button on the right
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Ajustes",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            // Centered Yeti mascot image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.yeti_complete_smiling),
                    contentDescription = "Logo de Yetify",
                    modifier = Modifier.size(250.dp) // Friendly mascot size
                )
            }

            // Grid of four action buttons (2 rows, 2 buttons per row)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First row: Activities and Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Actividades", R.drawable.ic_activities, onActivitiesClick, Modifier.weight(1f))
                    HomeButton("Progreso", R.drawable.ic_progress, onProgressClick, Modifier.weight(1f))
                }

                // Second row: Tasks and Readings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Mis Tareas", R.drawable.ic_tasks, onTasksClick, Modifier.weight(1f))
                    HomeButton("Lecturas", R.drawable.ic_readings, onReadingsClick, Modifier.weight(1f))
                }
            }

            // Pushes the bottom box to the bottom of the screen
            Spacer(modifier = Modifier.weight(1f))

            // Bottom decorative footer bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color(0xFFB3E5FC))
            )
        }

        // Animated Profile dialog displaying stored user info
        AnimatedVisibility(
            visible = showProfileDialog,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                confirmButton = {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("¡Cerrar perfil!", fontWeight = FontWeight.Bold)
                    }
                },
                // Title and body showing user information
                title = { Text("Mi Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Nombre de usuario: $userName")
                        Text("Correo: $email")
                        Text("Cumpleaños: $birthday")
                    }
                },
                // Dialog background color
                containerColor = Color(0xFFE1F5FE)
            )
        }

        // Animated Settings dialog with logout confirmation
        AnimatedVisibility(
            visible = showSettingsDialog,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showSettingsDialog = false
                        onLogout() // Triggers logout callback to parent
                    }) {
                        Text("Cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("Cancelar")
                    }
                },
                // Dialog title and confirmation message
                title = { Text("Ajustes", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas cerrar sesión de tu cuenta?") },
                // Dialog background color
                containerColor = Color(0xFFFFF8E1)
            )
        }
    }
}

@Composable
fun HomeButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Reusable composable for main navigation buttons
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(120.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4EB1CB)), // Themed blue
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        // Icon + Text stacked vertically
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Preview function to render the HomeScreen in Android Studio
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    HomeScreen(
        onActivitiesClick = {},
        onProgressClick = {},
        onTasksClick = {},
        onReadingsClick = {},
        onLogout = {},
        userPreferences = prefs
    )
}
