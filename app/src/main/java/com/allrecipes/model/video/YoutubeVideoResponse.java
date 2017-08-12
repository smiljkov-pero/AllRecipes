package com.allrecipes.model.video;

import com.allrecipes.model.PageInfo;
import com.allrecipes.model.YoutubeItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class YoutubeVideoResponse {

    @SerializedName("nextPageToken")
    public String nextPageToken;

    @SerializedName("prevPageToken")
    public String prevPageToken;

    @SerializedName("pageInfo")
    public PageInfo pageInfo;

    @SerializedName("items")
    public List<VideoItem> items;
}
