package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

public class CreditCrewResult {
    @SerializedName("id")
    int id;

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
