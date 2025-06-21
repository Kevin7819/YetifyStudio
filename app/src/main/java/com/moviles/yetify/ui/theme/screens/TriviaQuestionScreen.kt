package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.yetify.viewmodel.TriviaViewModel
import kotlin.random.Random

@Composable
fun TriviaQuestionScreen(navController: NavController, categoryId: Int) {
    val viewModel: TriviaViewModel = viewModel()
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    val score by viewModel.score.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.fetchQuestions(categoryId)
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF4EB1CB)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    if (isFinished) {
        if (score == questions.size) {
            TriviaWinScreen(navController, onRetry = { viewModel.reset(); viewModel.fetchQuestions(categoryId) })
        } else {
            TriviaLoseScreen(navController, onRetry = { viewModel.reset(); viewModel.fetchQuestions(categoryId) })
        }
        return
    }

    val question = questions[currentIndex]
    val options = remember(question) {
        (question.incorrect_answers + question.correct_answer).shuffled(Random(currentIndex))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EB1CB))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pregunta ${currentIndex + 1}/${questions.size}", color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = android.text.Html.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.height(24.dp))
        options.forEach { option ->
            Button(
                onClick = { viewModel.answer(option) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    android.text.Html.fromHtml(option, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                    color = Color(0xFF4EB1CB),
                    fontSize = 18.sp
                )
            }
        }
    }
}