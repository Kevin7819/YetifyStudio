package com.moviles.yetify.network
import com.moviles.yetify.models.Course
import com.moviles.yetify.models.LoginApiResponse
import com.moviles.yetify.models.LoginRequest
import com.moviles.yetify.models.LoginResponse
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.viewmodel.UserTaskViewModel
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
    @GET("api/UserTask")
    suspend fun getUserTasks(): List<UserTask>

    @GET("api/Course")
    suspend fun getCourses(): List<Course>

    //GET to get tasks per logged in user
    @GET("api/UserTask/user/{userId}")
    suspend fun getUserTasksByUserId(@Path("userId") userId: Int): List<UserTask>

    //GET to create a task per logged in user
    @POST("api/UserTask/user/{userId}")
    suspend fun createTaskByUser(@Path("userId") userId: Int,  @Body userTask: UserTask): Response<UserTask>

    // POST to create a new task
    @POST("api/UserTask")
    suspend fun addUserTask(@Body userTask: UserTask): UserTask

    // PUT to update an existing task by ID
    @PUT("api/UserTask/{id}")
    suspend fun updateUserTask(@Path("id") id: Int, @Body userTask: UserTask): UserTask

    // DELETE to delete a task by ID
    @DELETE("api/UserTask/{id}") suspend fun deleteUserTask(@Path("id") id: Int): Response<Unit>

    @POST("/api/Auth/Login")
    suspend fun login(@Body request: LoginRequest): Response<LoginApiResponse>
}