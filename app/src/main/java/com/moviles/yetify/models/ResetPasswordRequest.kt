package com.moviles.yetify.models;

data class ResetPasswordRequest(
        val email: String,
        val code: String,
        val newPassword: String,
        val confirmPassword: String
)