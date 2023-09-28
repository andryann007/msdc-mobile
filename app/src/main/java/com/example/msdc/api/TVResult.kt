package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVResult (
    @field:SerializedName("id") val id: Int? = 0,
    @field:SerializedName("poster_path") val posterPath: String? = null,
    @field:SerializedName("original_name") val name: String? = null
) : Parcelable