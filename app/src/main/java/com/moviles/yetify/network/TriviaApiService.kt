package com.moviles.yetify.network

import com.moviles.yetify.models.TriviaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String = "easy",
        @Query("type") type: String = "multiple"
    ): TriviaResponse
}