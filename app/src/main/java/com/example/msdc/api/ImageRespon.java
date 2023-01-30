package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ImageRespon {
    @SerializedName("backdrops")
    private final List<ImageResult> results = new ArrayList<>();

    public List<ImageResult> getResults() {
        return results;
    }
}
