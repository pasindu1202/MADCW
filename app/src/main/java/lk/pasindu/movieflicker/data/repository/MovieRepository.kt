package lk.pasindu.movieflicker.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import lk.pasindu.movieflicker.data.api.TmdbApiService
import lk.pasindu.movieflicker.data.model.Genre
import lk.pasindu.movieflicker.data.model.Movie
import lk.pasindu.movieflicker.data.model.MovieListResponse // Make sure MovieListResponse is imported
import lk.pasindu.movieflicker.data.model.GenreResponse // Make sure GenreResponse is imported
import retrofit2.HttpException
import java.io.IOException

class MovieRepository(private val api: TmdbApiService) {

    companion object {
        private const val TAG = "MovieRepository"
        private const val API_KEY = TmdbApiService.API_KEY
    }

    /**
     * Generic safe API call wrapper.
     * Reduces boilerplate for error handling in each specific fetch method.
     * It returns the successful response or null if an error occurs.
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T? {
        return withContext(Dispatchers.IO) {
            try {
                apiCall.invoke()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "HTTP Error: ${e.code()} - $errorBody - ${e.message()}", e)
                null // Return null on HTTP error
            } catch (e: IOException) {
                Log.e(TAG, "Network Error: ${e.message}", e)
                null // Return null on network error
            } catch (e: Exception) {
                Log.e(TAG, "Unknown Error: ${e.message}", e)
                null // Return null on any other unexpected error
            }
        }
    }

    // --- Specific Movie Category Fetch Methods ---
    // These methods now call the specific TmdbApiService methods and handle their results.

    suspend fun getPopularMovies(): List<Movie> {
        val response = safeApiCall { api.getPopularMovies(API_KEY) }
        return response?.results ?: emptyList()
    }

    suspend fun getTopRatedMovies(): List<Movie> {
        val response = safeApiCall { api.getTopRatedMovies(API_KEY) }
        return response?.results ?: emptyList()
    }

    suspend fun getUpcomingMovies(): List<Movie> {
        val response = safeApiCall { api.getUpcomingMovies(API_KEY) }
        return response?.results ?: emptyList()
    }

    suspend fun getNowPlayingMovies(): List<Movie> {
        val response = safeApiCall { api.getNowPlayingMovies(API_KEY) }
        return response?.results ?: emptyList()
    }

    // --- Existing Generic Methods (if still used elsewhere) ---
    // If you only use the specific methods above, you can remove this generic one.
    // However, if your search or other parts of the app dynamically build categories, keep it.
    /*
    suspend fun getMovies(category: String, genreId: String? = null): List<Movie> {
        val response = safeApiCall { api.getMoviesByCategory(category, genreId, API_KEY) }
        return response?.results ?: emptyList()
    }
    */

    // --- Existing Genre and Search Methods ---

    suspend fun getGenres(): List<Genre> {
        val response = safeApiCall { api.getGenres(API_KEY) } // Pass API_KEY
        return response?.genres ?: emptyList()
    }

    suspend fun searchMovies(query: String): List<Movie> {
        val response = safeApiCall { api.searchMovies(API_KEY, query) } // Pass API_KEY
        return response?.results ?: emptyList()
    }

    // You can add more specific movie detail fetching methods here if needed
    // suspend fun getMovieDetails(movieId: Int): Movie? {
    //     val response = safeApiCall { api.getMovieDetails(movieId, API_KEY) }
    //     return response
    // }
}