package lk.pasindu.movieflicker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.pasindu.movieflicker.data.model.Movie
import lk.pasindu.movieflicker.data.repository.MovieRepository

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _results = MutableLiveData<List<Movie>>()
    val results: LiveData<List<Movie>> = _results

    // ADD THIS LIVE DATA FOR LOADING STATE
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private var searchJob: Job? = null // To debounce search requests

    fun search(query: String) {
        searchJob?.cancel() // Cancel any ongoing search before starting a new one

        if (query.isBlank()) {
            _results.value = emptyList() // Clear results if query is empty
            _isLoading.value = false     // Stop loading if no query
            // _error.value = null          // Clear any previous error
            return
        }

        _isLoading.value = true // Set loading to true when a search starts
        // _error.value = null        // Clear any previous error

        searchJob = viewModelScope.launch {
            delay(300) // Debounce: Wait 300ms before making the API call
            // This prevents excessive API calls as the user types

            val movies = repository.searchMovies(query)

            _results.value = movies
            _isLoading.value = false // Set loading to false when results are received


        }
    }
}