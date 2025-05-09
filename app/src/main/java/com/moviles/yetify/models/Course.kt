package com.moviles.yetify.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int, //primary key
    @ColumnInfo(name = "nameCourse") val nameCourse: String,
    @ColumnInfo(name = "description") val description: String
)
