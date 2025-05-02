package com.moviles.yetify.models
import com.moviles.yetify.models.User
import com.moviles.yetify.models.Course

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserTask", //Name of the table in the BD
    foreignKeys = [
        ForeignKey(
            entity = User::class,  //Entity being referred to
            parentColumns = ["id"],//column primary in the table User
            childColumns = ["idUser"], // column of this entity that refers to User
            onDelete = ForeignKey.CASCADE // if the user is deleted, their tasks are deleted
        ),
        ForeignKey(
            entity = Course::class,//Entity being referred to
            parentColumns = ["id"],//column primary in the table Course
            childColumns = ["idCourse"],// column of this entity that refers to Course
            onDelete = ForeignKey.CASCADE// if the user is deleted, their course are deleted
        )
    ]
)

data class UserTask(
    @PrimaryKey(autoGenerate = true) val id: Int, //primary key
    @ColumnInfo(name = "idUser") val idUser: Int?, // ForeignKey
    @ColumnInfo(name = "idCourse") val idCourse: Int?, // ForeignKey
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "dueDate") val dueDate: String,
    @ColumnInfo(name = "status") val status: String
)
