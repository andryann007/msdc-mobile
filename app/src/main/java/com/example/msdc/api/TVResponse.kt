package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVResponse (
    @SerializedName("results") val result: ArrayList<TVResult>? = null
) : Parcelable