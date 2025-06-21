package com.moviles.yetify.models

data class TriviaResponse(
    val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)