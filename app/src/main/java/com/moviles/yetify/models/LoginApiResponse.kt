package com.moviles.yetify.models

/**
 * Wrapper class for login API response.
 * Matches structure: { isSuccess: true, message: "...", user: { ... } }
 */
data class LoginApiResponse(
    val isSuccess: Boolean,
    val message: String,
    val user: LoginResponse
)