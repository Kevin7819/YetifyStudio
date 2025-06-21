package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActivitiesScreen(
    onTriviaClick: () -> Unit,
    onAudiobooksClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¿Qué actividad quieres hacer?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onTriviaClick) {
            Text("Trivias")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAudiobooksClick) {
            Text("Audiolibros")
        }
    }
}