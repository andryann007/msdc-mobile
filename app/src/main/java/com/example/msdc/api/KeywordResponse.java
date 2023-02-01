package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class KeywordResponse {
    @SerializedName("keywords")
    private final List<KeywordResult> keywords = new ArrayList<>();

    public List<KeywordResult> getKeywords() {
        return keywords;
    }
}
