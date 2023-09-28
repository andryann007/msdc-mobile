package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCrewResult (
    @field:SerializedName("id") val id : Int? = 0,
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("profile_path") val profilePath: String? = null,
    @field:SerializedName("known_for_department") val jobDesk: String? = null,
    @field:SerializedName("gender") val gender : Int? = 0
) : Parcelable