package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class ImageResult {
    @SerializedName("file_path")
    String filePath;

    public String getFilePath() {
        return filePath;
    }
}
