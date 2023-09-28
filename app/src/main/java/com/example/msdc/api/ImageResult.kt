package com.example.msdc.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageResult (
    @field:SerializedName("file_path") val filePath: String? = null
) : Parcelable