package com.example.msdc;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieRespon {
    @SerializedName("results")
    private final List<MovieResult> results = new ArrayList<>();

    @SerializedName("total_pages")
    int totalPages;

    public List<MovieResult> getResult(){ return results; }
    public int getTotalPages(){ return totalPages; }
}
