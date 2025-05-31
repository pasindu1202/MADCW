// lk.pasindu.movieflicker.ui.search/SearchFragment.kt
package lk.pasindu.movieflicker.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import lk.pasindu.movieflicker.R
import lk.pasindu.movieflicker.databinding.FragmentSearchBinding
import lk.pasindu.movieflicker.ui.adapter.SearchAdapter
import lk.pasindu.movieflicker.data.api.TmdbApiClient // Import TmdbApiClient
import lk.pasindu.movieflicker.data.repository.MovieRepository // Import MovieRepository

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {

        val apiService = TmdbApiClient.apiService

        val repository = MovieRepository(apiService)
        SearchViewModelFactory(repository)
    }

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { movie ->
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie)
            findNavController().navigate(action)
        }

        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupSearchInput() {
        binding.editTextSearch.doAfterTextChanged { editable ->
            val query = editable.toString().trim()
            viewModel.search(query)
            binding.iconClear.isVisible = query.isNotEmpty()
            binding.iconMicrophone.isVisible = query.isEmpty()
        }
        binding.iconClear.isVisible = false
        binding.iconMicrophone.isVisible = true
    }

    private fun setupClickListeners() {
        binding.iconClear.setOnClickListener {
            binding.editTextSearch.text?.clear()
            binding.editTextSearch.requestFocus()
        }
    }

    private fun observeViewModel() {
        viewModel.results.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            updateUiVisibility(results.isEmpty(), viewModel.isLoading.value ?: false)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateUiVisibility(viewModel.results.value?.isEmpty() ?: true, isLoading)
        }
    }

    private fun updateUiVisibility(isResultsEmpty: Boolean, isLoading: Boolean) {
        binding.progressBarLoading.isVisible = isLoading
        binding.recyclerViewSearchResults.isVisible = !isLoading && !isResultsEmpty
        binding.layoutEmptyState.isVisible = !isLoading && isResultsEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}