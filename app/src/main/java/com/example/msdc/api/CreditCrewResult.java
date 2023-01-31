package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class CreditCrewResult {
    @SerializedName("id")
    int id;

    @SerializedName("credit_id")
    String creditId;

    @SerializedName("name")
    String name;

    @SerializedName("profile_path")
    String profilePath;

    @SerializedName("known_for_department")
    String jobDesk;

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

    public String getJobDesk() {
        return jobDesk;
    }

    public int getGender() {
        return gender;
    }
}
