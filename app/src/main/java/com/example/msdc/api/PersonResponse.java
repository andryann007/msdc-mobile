package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PersonResponse {
    @SerializedName("results")
    private final List<PersonResult> results = new ArrayList<>();

    @SerializedName("total_pages")
    int totalPages;

    public List<PersonResult> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
