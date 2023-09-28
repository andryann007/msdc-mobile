package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetails (
    @field:SerializedName("title") val title: String? = null,
    @field:SerializedName("homepage") val homepage: String? = null,
    @field:SerializedName("overview") val overview: String? = null,
    @field:SerializedName("runtime") val runtime: String? = null,
    @field:SerializedName("poster_path") val posterPath: String? = null,
    @field:SerializedName("vote_average") val voteAverage: String? = null,
    @field:SerializedName("release_date") val releaseDate: String? = null,
    @field:SerializedName("original_language") val language: String? = null,
    @field:SerializedName("backdrop_path") val backdropPath: String? = null,
    @field:SerializedName("status") val status: String? = null,
    @field:SerializedName("budget") val budget: String? = null,
    @field:SerializedName("revenue") val revenue: String? = null,
    @field:SerializedName("popularity") val popularity: String? = null,
    @field:SerializedName("tagline") val tagline: String? = null,
    @field:SerializedName("vote_count") val voteCount: String? = null,
    @field:SerializedName("genres") val genres: ArrayList<GenreResult>? = null
) : Parcelable