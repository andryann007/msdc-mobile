package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeasonResponse (
    @field:SerializedName("_id") val id: String? = null,
    @field:SerializedName("episodes") val episodeResults: ArrayList<EpisodeResult>? = null
) : Parcelable