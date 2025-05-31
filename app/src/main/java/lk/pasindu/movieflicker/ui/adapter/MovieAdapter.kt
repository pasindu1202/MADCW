package lk.pasindu.movieflicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lk.pasindu.movieflicker.R
import lk.pasindu.movieflicker.data.model.Movie
// Assuming your layout binding file is actually item_movie_card.xml
// based on the previous conversation, but your code here uses ItemMovieBinding.
// Make sure this import matches your actual layout file name (e.g., item_movie_card.xml -> ItemMovieCardBinding)
import lk.pasindu.movieflicker.databinding.ItemMovieBinding // Or ItemMovieCardBinding if that's your file

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : // Change to ItemMovieCardBinding if needed
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            // Set Movie Title
            binding.textViewMovieTitle.text = movie.title

            // Load Movie Poster
            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .placeholder(R.drawable.default_poster) // Good practice to have a placeholder
                .error(R.drawable.default_poster)
                .into(binding.imageViewMoviePoster)

            // --- Set Movie Rating (NEW) ---
            // Ensure your Movie data class has 'voteAverage: Double'
            binding.textViewRating.text = String.format("%.1f", movie.voteAverage)

            // Set click listener for the entire item
            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Ensure this matches your actual layout file name (e.g., ItemMovieCardBinding)
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun updateData(newMovies: List<Movie>) {
        this.movies = newMovies
        // For better performance, consider using DiffUtil instead of notifyDataSetChanged()
        notifyDataSetChanged()
    }
}