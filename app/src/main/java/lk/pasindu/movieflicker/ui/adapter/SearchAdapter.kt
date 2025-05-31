package lk.pasindu.movieflicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lk.pasindu.movieflicker.R
import lk.pasindu.movieflicker.data.model.Movie
import lk.pasindu.movieflicker.databinding.ItemSearchBinding

class SearchAdapter(
    private val onItemClick: (Movie) -> Unit
) : ListAdapter<Movie, SearchAdapter.SearchViewHolder>(DiffCallback()) {

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            val year = movie.releaseDate.take(4).ifEmpty { "N/A" }
            binding.textMovieTitle.text = "${movie.title} ($year)"

            Glide.with(binding.imagePoster.context)
                .load("https://image.tmdb.org/t/p/w185${movie.posterPath}")
                .error(R.drawable.default_poster)
                .into(binding.imagePoster)

            // Handle item click
            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}
