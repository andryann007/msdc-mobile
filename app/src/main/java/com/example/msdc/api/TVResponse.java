package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TVResponse {
    @SerializedName("results")
    private final List<TVResult> results = new ArrayList<>();

    public List<TVResult> getResult(){ return results; }
}
