package com.moviles.yetify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.models.TriviaQuestion
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TriviaViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<TriviaQuestion>>(emptyList())
    val questions: StateFlow<List<TriviaQuestion>> get() = _questions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> get() = _currentIndex

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> get() = _isFinished

    fun fetchQuestions(category: Int) {
        viewModelScope.launch {
            val response = RetrofitInstance.triviaApi.getQuestions(3, category)
            _questions.value = response.results
            _currentIndex.value = 0
            _score.value = 0
            _isFinished.value = false
        }
    }


    fun answer(selected: String) {
        val current = _questions.value.getOrNull(_currentIndex.value) ?: return
        if (selected == current.correct_answer) {
            _score.value += 1
        } else {
            _isFinished.value = true
        }
        if (_currentIndex.value < _questions.value.size - 1 && !_isFinished.value) {
            _currentIndex.value += 1
        } else if (!_isFinished.value) {
            _isFinished.value = true
        }
    }

    fun reset() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _score.value = 0
        _isFinished.value = false
    }

}