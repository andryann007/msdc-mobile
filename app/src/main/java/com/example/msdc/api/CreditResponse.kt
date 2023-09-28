package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditResponse(
    @field:SerializedName("cast") val cast: ArrayList<CreditCastResult>? = null,
    @field:SerializedName("crew") val crew: ArrayList<CreditCrewResult>? = null
) : Parcelable