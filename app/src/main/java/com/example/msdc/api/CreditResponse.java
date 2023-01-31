package com.example.msdc.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CreditResponse {
    @SerializedName("cast")
    private final List<CreditCastResult> cast = new ArrayList<>();

    @SerializedName("crew")
    private final List<CreditCrewResult> crew = new ArrayList<>();

    public List<CreditCastResult> getCast() {
        return cast;
    }

    public List<CreditCrewResult> getCrew() {
        return crew;
    }
}
