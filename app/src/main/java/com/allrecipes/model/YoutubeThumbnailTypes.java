package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeThumbnailTypes {
    @SerializedName("default")
    public YoutubeThumbnails defaultThumbnail;

    @SerializedName("medium")
    public YoutubeThumbnails mediumThumbnail;

    @SerializedName("high")
    public YoutubeThumbnails highThumbnail;

    @SerializedName("standard")
    public YoutubeThumbnails standard;

    @SerializedName("maxres")
    public YoutubeThumbnails maxres;
}
