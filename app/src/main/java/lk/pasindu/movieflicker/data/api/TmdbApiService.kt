package lk.pasindu.movieflicker.data.api

import lk.pasindu.movieflicker.data.model.GenreResponse
import lk.pasindu.movieflicker.data.model.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    companion object {
        const val API_KEY = "44dff87598bad1f3073f7e13db938e73"
    }


    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListResponse

    // --- Existing Generic Methods ---
    @GET("movie/{category}")
    suspend fun getMoviesByCategory(
        @Path("category") category: String,
        @Query("with_genres") genre: String? = null,
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListResponse



    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = API_KEY
    ): GenreResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("query") query: String
    ): MovieListResponse
}