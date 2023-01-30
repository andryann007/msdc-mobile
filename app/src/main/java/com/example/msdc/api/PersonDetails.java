package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class PersonDetails {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("biography")
    String biography;

    @SerializedName("popularity")
    int popularity;

    @SerializedName("place_of_birth")
    String placeOfBirth;

    @SerializedName("profile_path")
    String profilePath;

    @SerializedName("adult")
    boolean adult;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public boolean isAdult() {
        return adult;
    }
}
