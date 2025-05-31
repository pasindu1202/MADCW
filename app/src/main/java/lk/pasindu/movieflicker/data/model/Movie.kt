package lk.pasindu.movieflicker.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val overview: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,
    val adult: Boolean = false,
    val popularity: Double = 0.0,
    @SerializedName("original_title")
    val originalTitle: String = "",
    @SerializedName("original_language")
    val originalLanguage: String = ""
) : Parcelable