package com.moviles.yetify.models

import java.util.Date
/**
 * Represents the user data returned after successful login.
 */
data class LoginResponse(
    val id: Int,
    val userName: String,
    val email: String,
    val birthday: Date,
    val registrationDate: Date,
    val token: String
)