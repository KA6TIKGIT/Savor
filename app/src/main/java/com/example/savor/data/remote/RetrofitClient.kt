package com.example.savor.data.remote

import com.example.savor.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://world.openfoodfacts.org/"

    // Open Food Facts asks apps to identify themselves with a User-Agent
    private val userAgentInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Savor/1.0 (Android student project)")
            .build()
        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        // Network traffic should never be logged by a release build.
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OpenFoodFactsApi by lazy {
        retrofit("https://world.openfoodfacts.org/").create(OpenFoodFactsApi::class.java)
    }

    val searchApi: SearchApi by lazy {
        retrofit("https://search.openfoodfacts.org/").create(SearchApi::class.java)
    }
}
