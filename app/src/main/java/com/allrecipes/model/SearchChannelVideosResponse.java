package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchChannelVideosResponse {

    @SerializedName("nextPageToken")
    public String nextPageToken;

    @SerializedName("prevPageToken")
    public String prevPageToken;

    @SerializedName("regionCode")
    public String regionCode;

    @SerializedName("pageInfo")
    public PageInfo pageInfo;

    @SerializedName("items")
    public List<YoutubeItem> items;
}
