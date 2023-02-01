package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class KeywordResult {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
