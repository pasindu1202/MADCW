package lk.pasindu.movieflicker.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import lk.pasindu.movieflicker.data.api.TmdbApiClient
import lk.pasindu.movieflicker.data.model.Movie
import lk.pasindu.movieflicker.data.repository.MovieRepository
import retrofit2.HttpException // For more specific error handling
import java.io.IOException // For network errors
import android.util.Log // For logging errors

// Define the MovieCategory enum here or in a separate shared file
enum class MovieCategory {
    POPULAR,
    TOP_RATED,
    UPCOMING,
    NOW_PLAYING
}

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {


    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Initial load when the ViewModel is created (optional, if you want a default on startup)
    init {
        loadMovies(MovieCategory.POPULAR)
    }

    /**
     * Loads movies based on the specified category.
     * Manages loading state and error handling.
     */
    fun loadMovies(category: MovieCategory) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true before starting
            _errorMessage.value = null // Clear any previous error message

            try {
                val result = when (category) {
                    MovieCategory.POPULAR -> repository.getPopularMovies()
                    MovieCategory.TOP_RATED -> repository.getTopRatedMovies()
                    MovieCategory.UPCOMING -> repository.getUpcomingMovies()
                    MovieCategory.NOW_PLAYING -> repository.getNowPlayingMovies()
                }
                _movies.value = result // Update LiveData with fetched movies
            } catch (e: IOException) {
                // Handle network errors (e.g., no internet connection)
                Log.e("HomeViewModel", "Network error: ${e.message}", e)
                _errorMessage.value = "Network error: Please check your internet connection."
                _movies.value = emptyList() // Clear movies on network error
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 404, 401, 500 from API)
                Log.e("HomeViewModel", "HTTP error: ${e.code()} - ${e.message()}", e)
                _errorMessage.value = "API Error: ${e.code()} - ${e.message()}"
                _movies.value = emptyList() // Clear movies on API error
            } catch (e: Exception) {
                // Handle any other unexpected errors
                Log.e("HomeViewModel", "Unexpected error: ${e.message}", e)
                _errorMessage.value = "An unexpected error occurred: ${e.message}"
                _movies.value = emptyList() // Clear movies on unexpected error
            } finally {
                _isLoading.value = false // Always set loading to false when done
            }
        }
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}