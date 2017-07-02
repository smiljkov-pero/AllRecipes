package com.allrecipes.model.video;

import com.allrecipes.model.PageInfo;
import com.allrecipes.model.YoutubeItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class YoutubeVideoResponse {

    @SerializedName("pageInfo")
    public PageInfo pageInfo;

    @SerializedName("items")
    public ArrayList<VideoItem> items;
}
