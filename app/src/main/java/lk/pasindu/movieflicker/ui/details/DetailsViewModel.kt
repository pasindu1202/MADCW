package lk.pasindu.movieflicker.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lk.pasindu.movieflicker.data.api.TmdbApiClient
import lk.pasindu.movieflicker.data.model.Movie
import lk.pasindu.movieflicker.data.repository.MovieRepository

class DetailsViewModel : ViewModel() {

    private val repository = MovieRepository(TmdbApiClient.apiService)

    private val _genreNames = MutableLiveData<String>()
    val genreNames: LiveData<String> get() = _genreNames

    fun loadGenresForMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                val genres = repository.getGenres()
                val genreMap = genres.associateBy({ it.id }, { it.name })
                val names = movie.genreIds?.mapNotNull { genreMap[it] }?.joinToString(", ") ?: "Unknown"
                _genreNames.value = names
            } catch (e: Exception) {
                _genreNames.value = "Unknown"
            }
        }
    }
}
