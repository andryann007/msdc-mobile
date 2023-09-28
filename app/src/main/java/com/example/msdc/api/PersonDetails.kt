package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetails (
    @field:SerializedName("id") val id: Int? = 0,
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("biography") val biography: String? = null,
    @field:SerializedName("popularity") val popularity: Int? = 0,
    @field:SerializedName("place_of_birth") val placeOfBirth: String? = null,
    @field:SerializedName("profile_path") val profilePath: String? = null,
    @field:SerializedName("adult") val isAdult: Boolean? = false
) : Parcelable