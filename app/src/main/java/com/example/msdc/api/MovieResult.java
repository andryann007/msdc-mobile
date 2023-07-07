package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class MovieResult {
    @SerializedName("id")
    int id;

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("title")
    String title;

    public int getId(){ return id; }

    public String getPosterPath(){ return posterPath; }

    public String getTitle(){ return title; }
}
