package com.moviles.yetify.network
import com.google.gson.JsonObject
import com.moviles.yetify.models.ApiResponse
import com.moviles.yetify.models.Book
import com.moviles.yetify.models.Course
import com.moviles.yetify.models.ForgotPasswordRequest
import com.moviles.yetify.models.LoginApiResponse
import com.moviles.yetify.models.LoginRequest
import com.moviles.yetify.models.LoginResponse
import com.moviles.yetify.models.RegisterRequest
import com.moviles.yetify.models.ResetPasswordRequest
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.viewmodel.UserTaskViewModel
import com.moviles.yetify.models.User
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
import retrofit2.http.Query

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
    suspend fun updateUserTask(@Path("id") id: Int?, @Body userTask: UserTask): UserTask

    // DELETE to delete a task by ID
    @DELETE("api/UserTask/{id}") suspend fun deleteUserTask(@Path("id") id: Int): Response<Unit>

    @POST("/api/Auth/Login")
    suspend fun login(@Body request: LoginRequest): Response<LoginApiResponse>

    @POST("/api/Auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse>

    @POST("/api/Auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>


    //books api
//    @GET("api/Books")
//    suspend fun getListBooks():List<Book>
//
//    @GET("api/Books/{id}")
//    suspend fun getBook(@Path("id") bookId: Int):Book?
//
//    @GET("api/Books/search/{search}")
//    suspend fun getSearchBook(@Path("search") bookId: String):List<Book>

    // GET api/books?userId=1
    @GET("api/books")
    suspend fun getAllBooks(@Query("userId") userId: Int): List<Book>

    // GET api/books/{id}?userId=1
    @GET("api/books/{id}")
    suspend fun getBookById(@Path("id") id: Int, @Query("userId") userId: Int): Book

    // GET api/books/search/{search}?userId=1
    @GET("api/books/search/{search}")
    suspend fun searchBooks(@Path("search") search: String, @Query("userId") userId: Int): List<Book>

    // PUT api/books/{id}/progress?userId=1
    @PUT("api/books/{id}/progress")
    suspend fun updateProgress(
        @Path("id") id: Int,
        @Query("userId") userId: Int,
        @Body body: JsonObject
    ):Response<JsonObject>


    @POST("/api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>

    @GET("api/User/{id}")
    suspend fun getUserById(@Path("id") id: Int): User

}