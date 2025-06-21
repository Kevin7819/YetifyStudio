package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.yetify.models.TriviaQuestion

@Composable
fun TriviaGameScreen(
    questions: List<TriviaQuestion>,
    onFinish: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val currentQuestion = questions.getOrNull(currentIndex)

    if (questions.isEmpty()) {
        Text("No se pudieron cargar preguntas. Intenta de nuevo.")
        Button(onClick = onFinish) { Text("Volver") }
        return
    }

    if (currentQuestion == null) {
        Text("¡Juego terminado! Puntaje: $score/${questions.size}")
        Button(onClick = onFinish) { Text("Volver") }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pregunta ${currentIndex + 1} de ${questions.size}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(currentQuestion.question.text, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        val options = remember(currentQuestion) {
            (currentQuestion.incorrectAnswers + currentQuestion.correctAnswer).shuffled()
        }

        options.forEach { option ->
            Button(
                onClick = {
                    if (option == currentQuestion.correctAnswer) score++
                    currentIndex++
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(option)
            }
        }
    }
}