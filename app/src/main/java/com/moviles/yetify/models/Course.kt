package com.moviles.yetify.models

import androidx.room.PrimaryKey

data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int?
)
