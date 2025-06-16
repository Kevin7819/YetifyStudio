package com.moviles.yetify.network

import android.app.Application
import com.moviles.yetify.common.Constants.API_BASE_URL
import com.moviles.yetify.datastore.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
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

    // Retrofit instance that uses the interceptor
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}