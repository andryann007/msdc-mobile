package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCastResult (
    @field:SerializedName("id") val id : Int? = 0,
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("profile_path") val profilePath: String? = null,
    @field:SerializedName("character") val character: String? = null,
    @field:SerializedName("gender") val gender : Int? = 0
) : Parcelable