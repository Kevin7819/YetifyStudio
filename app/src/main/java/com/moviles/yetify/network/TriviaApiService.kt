package com.moviles.yetify.network

import com.moviles.yetify.models.TriviaQuestion
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("questions")
    suspend fun getQuestions(
        @Query("categories") categories: String? = null,
        @Query("limit") limit: Int = 5
    ): List<TriviaQuestion>
}