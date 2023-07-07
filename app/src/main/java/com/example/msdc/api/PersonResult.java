package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class PersonResult {
    @SerializedName("id")
    int id;

    @SerializedName("profile_path")
    String posterPath;

    @SerializedName("name")
    String name;

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getName() {
        return name;
    }
}
