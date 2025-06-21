package com.moviles.yetify.models

data class TriviaQuestion(
    val category: String,
    val id: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val question: QuestionText,
    val tags: List<String>,
    val type: String,
    val difficulty: String,
    val regions: List<String>,
    val isNiche: Boolean
)

data class QuestionText(
    val text: String
)