package com.moviles.yetify.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    var showProfileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val userName by userPreferences.userName.collectAsState(initial = "")
    val email by userPreferences.email.collectAsState(initial = "")
    val birthday by userPreferences.birthday.collectAsState(initial = "")
    val registrationDate by userPreferences.registrationDate.collectAsState(initial = "")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB3E5FC))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showProfileDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Perfil",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }

                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Ajustes",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.yeti_complete_smiling),
                    contentDescription = "Logo de Yetify",
                    modifier = Modifier.size(250.dp)
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Actividades", R.drawable.ic_activities, onActivitiesClick, Modifier.weight(1f))
                    HomeButton("Progreso", R.drawable.ic_progress, onProgressClick, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Mis Tareas", R.drawable.ic_tasks, onTasksClick, Modifier.weight(1f))
                    HomeButton("Lecturas", R.drawable.ic_readings, onReadingsClick, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color(0xFFB3E5FC))
            )
        }

        // Nuevo ProfileDialog mejorado
        AnimatedVisibility(
            visible = showProfileDialog,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            userName?.let {
                email?.let { it1 ->
                    birthday?.let { it2 ->
                        ProfileDialog(
                            userName = it,
                            email = it1,
                            birthday = it2,
                            onDismiss = { showProfileDialog = false }
                        )
                    }
                }
            }
        }

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
                        onLogout()
                    }) {
                        Text("Cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Ajustes", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas cerrar sesión de tu cuenta?") },
                containerColor = Color(0xFFFFF8E1)
            )
        }
    }
}

@Composable
private fun ProfileDialog(
    userName: String,
    email: String,
    birthday: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFFE1F5FE),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF00796B)
                )
                Text(
                    text = "Mi Perfil Yeti",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "Nombre Yeti",
                    value = userName,
                    iconColor = Color(0xFF0288D1)
                )

                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = "Correo Yeti",
                    value = email,
                    iconColor = Color(0xFFD32F2F)
                )

                ProfileInfoItem(
                    icon = Icons.Default.Cake,
                    label = "Cumpleaños Yeti",
                    value = birthday,
                    iconColor = Color(0xFF7B1FA2)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4DB6AC),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¡Listo!")
            }
        }
    )
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF616161)
                ),
                fontSize = 14.sp
            )
            Text(
                text = value.ifEmpty { "No especificado" },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF212121),
                    fontWeight = FontWeight.Medium
                ),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
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
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(120.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4EB1CB)),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
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