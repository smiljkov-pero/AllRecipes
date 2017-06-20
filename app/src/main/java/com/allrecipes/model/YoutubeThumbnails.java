package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeThumbnails {
    @SerializedName("url")
    public String url;

    @SerializedName("width")
    public String width;

    @SerializedName("height")
    public String height;
}
