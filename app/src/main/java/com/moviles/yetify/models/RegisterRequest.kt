package com.moviles.yetify.models

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String,
    val birthday: String // YYYY-MM-DD
)
