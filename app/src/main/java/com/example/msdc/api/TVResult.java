package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class TVResult {
    @SerializedName("id")
    int id;

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("original_name")
    String name;

    @SerializedName("vote_average")
    double voteAverage;

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getId(){ return id; }

    public String getPosterPath(){ return posterPath; }

    public String getName(){ return name; }
}
