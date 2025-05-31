package lk.pasindu.movieflicker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import lk.pasindu.movieflicker.data.repository.MovieRepository

class HomeViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}