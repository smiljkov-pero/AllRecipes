package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Vladimir on 11/15/2016.
 */

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
    public ArrayList<Youtube> items;
}
