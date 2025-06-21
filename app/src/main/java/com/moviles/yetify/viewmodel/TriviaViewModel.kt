package com.moviles.yetify.ui.theme.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.models.TriviaQuestion
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TriviaViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<TriviaQuestion>>(emptyList())
    val questions: StateFlow<List<TriviaQuestion>> = _questions

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

fun fetchQuestions(category: String) {
    viewModelScope.launch {
        _loading.value = true
        try {
            val apiCategory = when (category) {
                "Ciencias" -> "science"
                "Historia" -> "history"
                "Arte y Literatura" -> "arts_and_literature"
                "Música" -> "music"
                "Geografía" -> "geography"
                "Cultura" -> "society_and_culture"
                "Deportes" -> "sport_and_leisure"
                else -> "general_knowledge"
            }
            val result = RetrofitInstance.triviaApi.getQuestions(apiCategory)
            _questions.value = result
        } catch (e: Exception) {
            println("Error al obtener preguntas: ${e.message}")
            _questions.value = emptyList()
        }
        _loading.value = false
    }
}
}