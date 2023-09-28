package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieResponse (
    @field:SerializedName("results") val result: ArrayList<MovieResult>? = null
) : Parcelable