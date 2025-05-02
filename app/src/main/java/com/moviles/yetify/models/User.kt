package com.moviles.yetify.models

import androidx.room.PrimaryKey

data class User(
    @PrimaryKey(autoGenerate = true) val id: Int?
)
