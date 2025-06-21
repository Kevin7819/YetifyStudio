package com.moviles.yetify.models

import androidx.room.PrimaryKey
import java.util.Date
/**
 * Data class representing a user object received from the API.
 * Matches the structure of the C# UserDto use- in responses (excluding password for security).
 */
data class User(
    val id: Int,                        // Unique user identifier
    val userName: String,              // Display name
    val email: String,                 // User's email address
    val birthday: Date,              // Date of birth
    val registrationDate: Date,      // Date the user registered
    val token: String                  // JWT token received from login (manually added)
)