package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EpisodeResult (
    @field:SerializedName("id") val id: Int? = 0,
    @field:SerializedName("air_date") val airDate: String? = null,
    @field:SerializedName("episode_number") val episodeNumber: Int? = 0,
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("overview") val overview: String? = null,
    @field:SerializedName("runtime") val runtime: Int? = 0,
    @field:SerializedName("season_number") val seasonNumber: Int? = 0,
    @field:SerializedName("still_path") val stillPath: String? = null
) : Parcelable