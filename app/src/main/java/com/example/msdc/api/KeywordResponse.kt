package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KeywordResponse (
    @field:SerializedName("keywords") val keywords: ArrayList<KeywordResult>? = null
) : Parcelable