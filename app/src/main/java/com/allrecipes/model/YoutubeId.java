package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeId {
    @SerializedName("kind")
    public String kind;

    @SerializedName("videoId")
    public String videoId;

    @SerializedName("channelId")
    public String channelId;

    @SerializedName("playlistId")
    public String playlistId;
}
