package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private final List<MovieResult> results = new ArrayList<>();

    public List<MovieResult> getResult(){ return results; }
}
