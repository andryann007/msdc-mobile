package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVDetails (
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("poster_path") val posterPath: String? = null,
    @field:SerializedName("backdrop_path") val backdropPath: String? = null,
    @field:SerializedName("episode_run_time") val episodeRuntime: IntArray,
    @field:SerializedName("first_air_date") val firstAirDate: String? = null,
    @field:SerializedName("last_air_date") val lastAirDate: String? = null,
    @field:SerializedName("number_of_episodes") val numberOfEpisodes: Int? = 0,
    @field:SerializedName("number_of_seasons") val numberOfSeasons: Int? = 0,
    @field:SerializedName("original_language") val originalLanguage: String? = null,
    @field:SerializedName("overview") val overview: String? = null,
    @field:SerializedName("popularity") val popularity: String? = null,
    @field:SerializedName("status") val status: String? = null,
    @field:SerializedName("tagline") val tagline: String? = null,
    @field:SerializedName("vote_average") val voteAverage: String? = null,
    @field:SerializedName("vote_count") val voteCount: String? = null,
    @field:SerializedName("homepage") val homepage: String? = null,
    @field:SerializedName("genres") val genres: ArrayList<GenreResult>? = null
) : Parcelable