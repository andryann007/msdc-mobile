package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreResult (
    @field:SerializedName("id") val id: Int? = 0,
    @field:SerializedName("name") val name: String? = null
) : Parcelable