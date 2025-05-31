package lk.pasindu.movieflicker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible // For easier visibility management
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip // Import Chip
import lk.pasindu.movieflicker.BuildConfig
import lk.pasindu.movieflicker.R
import lk.pasindu.movieflicker.databinding.FragmentHomeBinding
import lk.pasindu.movieflicker.ui.adapter.MovieAdapter
import lk.pasindu.movieflicker.data.api.TmdbApiClient // Ensure this is imported if HomeViewModel uses it
import lk.pasindu.movieflicker.data.repository.MovieRepository // Ensure this is imported

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieAdapter: MovieAdapter


    private val viewModel: HomeViewModel by viewModels {
        val apiService = TmdbApiClient.apiService
        val repository = MovieRepository(apiService)
        HomeViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()
        setupChips()
        setupSwipeRefresh()
        observeViewModel()


        val initialCheckedChipId = binding.chipGroupFilters.checkedChipId
        if (initialCheckedChipId == View.NO_ID) {
            binding.chipPopular.isChecked = true
        } else {
            binding.chipGroupFilters.findViewById<Chip>(initialCheckedChipId)?.let {
                loadMoviesForChip(it.id)
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) { selectedMovie ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(selectedMovie)
            findNavController().navigate(action)
        }
        binding.recyclerViewMovies.adapter = movieAdapter
    }

    private fun setupChips() {
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == View.NO_ID) { // No chip selected
                return@setOnCheckedChangeListener
            }
            loadMoviesForChip(checkedId)
        }
    }

    // Helper function to load movies based on chip ID
    private fun loadMoviesForChip(chipId: Int) {
        when (chipId) {
            R.id.chipPopular -> viewModel.loadMovies(MovieCategory.POPULAR)
            R.id.chipTopRated -> viewModel.loadMovies(MovieCategory.TOP_RATED)
            R.id.chipUpcoming -> viewModel.loadMovies(MovieCategory.UPCOMING) // <-- ADDED THIS
            R.id.chipNowPlaying -> viewModel.loadMovies(MovieCategory.NOW_PLAYING) // <-- ADDED THIS
            else -> {
                Log.w("HomeFragment", "Unknown chip ID: $chipId")
                // Fallback to popular or show an error
                viewModel.loadMovies(MovieCategory.POPULAR)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Get the currently checked chip and reload its data
            val checkedChipId = binding.chipGroupFilters.checkedChipId
            if (checkedChipId != View.NO_ID) {
                loadMoviesForChip(checkedChipId)
            } else {
                // If no chip is checked (shouldn't happen with singleSelection=true and initial check)
                viewModel.loadMovies(MovieCategory.POPULAR) // Default refresh
            }
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.updateData(movies)
            updateUiVisibility(movies.isEmpty(), viewModel.isLoading.value ?: false)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateUiVisibility(viewModel.movies.value?.isEmpty() ?: true, isLoading)
        }

        // Optional: Observe error messages from ViewModel
        // viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
        //     if (message != null) {
        //         Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        //         viewModel.clearErrorMessage() // Clear message after showing
        //     }
        // }
    }

    private fun updateUiVisibility(isResultsEmpty: Boolean, isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading // Show progress bar only when loading
        binding.swipeRefreshLayout.isRefreshing = isLoading // Control pull-to-refresh animation

        // Show RecyclerView if not loading AND results are not empty
        binding.recyclerViewMovies.isVisible = !isLoading && !isResultsEmpty

        // Show empty state if not loading AND results ARE empty
        binding.layoutEmptyState.isVisible = !isLoading && isResultsEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference
    }
}