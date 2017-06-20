package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeItem {
    @SerializedName("id")
    public YoutubeId id;

    @SerializedName("snippet")
    public YoutubeSnipped snippet;
}
