package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieDetails {
    @SerializedName("title")
    String title;

    @SerializedName("homepage")
    String homepage;

    @SerializedName("overview")
    String overview;

    @SerializedName("runtime")
    String runtime;

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("vote_average")
    String voteAverage;

    @SerializedName("release_date")
    String releaseDate;

    @SerializedName("original_language")
    String language;

    @SerializedName("backdrop_path")
    String backdropPath;

    @SerializedName("status")
    String status;

    @SerializedName("budget")
    String budget;

    @SerializedName("revenue")
    String revenue;

    @SerializedName("popularity")
    String popularity;

    @SerializedName("tagline")
    String tagline;

    @SerializedName("vote_count")
    String voteCount;

    @SerializedName("genres")
    private final List<GenreResult> genres = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getOverview() {
        return overview;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getStatus() {
        return status;
    }

    public String getBudget() {
        return budget;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getTagline() {
        return tagline;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public List<GenreResult> getGenres() {
        return genres;
    }
}
