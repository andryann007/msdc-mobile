package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class CreditCastResult {
    @SerializedName("id")
    int id;

    @SerializedName("credit_id")
    String creditId;

    @SerializedName("name")
    String name;

    @SerializedName("profile_path")
    String profilePath;

    @SerializedName("character")
    String character;

    @SerializedName("gender")
    int gender;

    public int getId() {
        return id;
    }

    public String getCreditId() {
        return creditId;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getCharacter() {
        return character;
    }

    public int getGender() {
        return gender;
    }
}
