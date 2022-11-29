package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TVRespon {
    @SerializedName("results")
    private final List<TVResult> results = new ArrayList<>();

    @SerializedName("total_pages")
    int totalPages;

    public List<TVResult> getResult(){ return results; }
    public int getTotalPages(){ return totalPages; }
}
