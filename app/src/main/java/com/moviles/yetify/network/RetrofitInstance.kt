package com.moviles.yetify.network

import android.app.Application
import com.google.gson.GsonBuilder
import com.moviles.yetify.common.Constants.API_BASE_URL
import com.moviles.yetify.datastore.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton for Retrofit API service, including an interceptor that attaches JWT token if available.
 */
object RetrofitInstance {

    private lateinit var userPreferences: UserPreferences

    /**
     * Initialize with context to access DataStore before using the API.
     */
    fun init(application: Application) {
        userPreferences = UserPreferences(application.applicationContext)
    }

    // Create a custom OkHttpClient with JWT interceptor
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                val token = runBlocking { userPreferences.token.firstOrNull() }

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    // Retrofit instance that uses the interceptor and custom Gson for date handling
    val api: ApiService by lazy {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create()

        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    val triviaApi: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://the-trivia-api.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }
}