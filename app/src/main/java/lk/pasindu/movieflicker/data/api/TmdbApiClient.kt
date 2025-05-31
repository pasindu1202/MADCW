package lk.pasindu.movieflicker.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Import this
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApiClient {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Create a logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }

    // Build an OkHttpClient with the logging interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Add the custom OkHttpClient here
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}