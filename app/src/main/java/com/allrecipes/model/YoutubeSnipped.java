package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeSnipped {
    @SerializedName("publishedAt")
    public String publishedAt;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("channelTitle")
    public String channelTitle;

    @SerializedName("thumbnails")
    public YoutubeThumbnailTypes thumbnails;
}
