package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GenreResponse {
    @SerializedName("genres")
    private final List<GenreResult> genreResults = new ArrayList<>();

    public List<GenreResult> getGenreResults() {
        return genreResults;
    }
}
