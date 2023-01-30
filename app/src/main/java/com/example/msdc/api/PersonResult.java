package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class PersonResult {
    @SerializedName("id")
    int id;

    @SerializedName("profile_path")
    String posterPath;

    @SerializedName("adult")
    boolean adult;

    @SerializedName("name")
    String name;

    @SerializedName("popularity")
    double popularity;

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getName() {
        return name;
    }

    public double getPopularity() {
        return popularity;
    }
}
