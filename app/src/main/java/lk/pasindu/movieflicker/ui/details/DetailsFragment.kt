package lk.pasindu.movieflicker.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import lk.pasindu.movieflicker.R
import lk.pasindu.movieflicker.databinding.FragmentDetailsBinding
import java.text.DecimalFormat // For formatting the vote average

class DetailsFragment : Fragment() {

    // ViewModel for fetching additional data like genre names
    private val viewModel: DetailsViewModel by viewModels()
    // Safe Args to retrieve the Movie object passed from the previous fragment
    private val args: DetailsFragmentArgs by navArgs()

    // View binding for safe access to layout views
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = args.movie // Get the Movie object from navigation arguments

        // Populate basic movie details directly from the passed Movie object
        binding.textTitle.text = movie.title
        // Extract and display the release year
        binding.textReleaseYear.text = movie.releaseDate.take(4)
        binding.textOverview.text = movie.overview

        // Display the movie poster using Glide
        // Check if posterPath is not null before loading
        if (!movie.posterPath.isNullOrEmpty()) {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .placeholder(R.drawable.default_poster) // Optional: show a placeholder while loading
                .error(R.drawable.default_poster) // Show default_poster if loading fails
                .into(binding.imageBackdrop)
        } else {
            // If posterPath is null or empty, just set the default poster
            binding.imageBackdrop.setImageResource(R.drawable.default_poster)
        }

        // Display the movie backdrop image (if available and you have an ImageView for it in XML)
        // Ensure your fragment_details.xml has an ImageView with ID 'imageBackdrop'
        // And your Movie data class has 'backdropPath'
        if (!movie.backdropPath.isNullOrEmpty()) {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280${movie.backdropPath}") // Use a larger size for backdrop
                .placeholder(R.drawable.default_poster) // Optional: placeholder for backdrop
                .error(R.drawable.default_poster) // Show default_backdrop if loading fails
                .into(binding.imageBackdrop) // Assuming you have an ImageView with id 'imageBackdrop'
        } else {
            // If backdropPath is null or empty, just set a default backdrop
            binding.imageBackdrop.setImageResource(R.drawable.default_poster)
        }


        // Display and format the vote average
        val decimalFormat = DecimalFormat("#.#") // Formats to one decimal place
        val formattedVoteAverage = decimalFormat.format(movie.voteAverage)
        // Assuming you have a TextView with ID 'textRating'
        binding.textRating.text = getString(R.string.movie_rating_format, formattedVoteAverage)

        // Optional: If you have a RatingBar in your XML (e.g., id 'ratingBar')
        // TMDb ratings are out of 10, so divide by 2 for a 5-star rating bar
        // binding.ratingBar.rating = (movie.voteAverage / 2).toFloat()
        // binding.ratingBar.setIsIndicator(true) // Make it read-only


        // Ask ViewModel to fetch genre names (this is asynchronous)
        viewModel.loadGenresForMovie(movie)

        // Observe genre names and display them once loaded
        viewModel.genreNames.observe(viewLifecycleOwner) { genreText ->
            binding.textGenre.text = genreText
        }

        // Optional: Set up a back button if you have one in your layout or toolbar
        // For example, if you have an ImageView with id 'iconBack'
        // binding.iconBack.setOnClickListener {
        //     findNavController().navigateUp() // Navigates back to the previous destination
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to prevent memory leaks
    }
}