package com.moviles.yetify.models

/**
 * Represents the user data returned after successful login.
 */
data class LoginResponse(
    val id: Int,
    val userName: String,
    val email: String,
    val role: String,
    val birthday: String,
    val registrationDate: String,
    val token: String
)