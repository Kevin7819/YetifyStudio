package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainMenuScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Tareas",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Buttons
            MenuButton("Añadir nueva tarea", onClick = { navController.navigate("addTask") })
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton("Lista de tareas", onClick = { navController.navigate("taskList") })
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton("Calendario de tareas", onClick = { navController.navigate("calendar") })
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton("Volver", onClick = { navController.popBackStack() })
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}