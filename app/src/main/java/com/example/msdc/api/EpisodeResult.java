package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class EpisodeResult {
    @SerializedName("air_date")
    String airDate;

    @SerializedName("episode_number")
    int episodeNumber;

    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("overview")
    String overview;

    @SerializedName("runtime")
    int runtime;

    @SerializedName("season_number")
    int seasonNumber;

    @SerializedName("still_path")
    String stillPath;

    public String getAirDate() {
        return airDate;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public String getStillPath() {
        return stillPath;
    }
}
