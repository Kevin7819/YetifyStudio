package com.moviles.yetify.network
import com.moviles.yetify.models.UserTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {

    // GET to get the user's task list
    @GET("api/usertask")
    suspend fun getUserTasks(): List<UserTask>

    // POST to create a new task
    @POST("api/usertask")
    suspend fun addUserTask(@Body userTask: UserTask): UserTask

    // PUT to update an existing task by ID
    @PUT("api/usertask/{id}")
    suspend fun updateUserTask(@Path("id") id: Int, @Body userTask: UserTask): UserTask

    // DELETE to delete a task by ID
    @DELETE("api/usertask/{id}") suspend fun deleteUserTask(@Path("id") id: Int): Response<Unit>

}