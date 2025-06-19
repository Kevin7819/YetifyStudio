package com.moviles.yetify.models

import androidx.room.PrimaryKey
/**
 * Data class representing a user object received from the API.
 * Matches the structure of the C# UserDto used in responses (excluding password for security).
 */
data class User(
    val id: Int,                        // Unique user identifier
    val userName: String,              // Display name
    val email: String,                 // User's email address
    val birthday: String,              // Date of birth (as ISO string, e.g., "2000-01-01")
    val registrationDate: String,      // Date the user registered (also ISO format)
    val token: String                  // JWT token received from login (manually added)
)