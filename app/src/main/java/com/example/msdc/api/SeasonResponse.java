package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SeasonResponse {
    @SerializedName("_id")
    String id;

    @SerializedName("episodes")
    ArrayList<EpisodeResult> episodeResults;

    public String getId() {
        return id;
    }

    public ArrayList<EpisodeResult> getEpisodeResults() {
        return episodeResults;
    }
}
