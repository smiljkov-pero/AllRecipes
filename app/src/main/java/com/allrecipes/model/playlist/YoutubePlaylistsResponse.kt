package com.allrecipes.model.playlist

import com.allrecipes.model.PageInfo
import com.google.gson.annotations.SerializedName

class YoutubePlaylistsResponse {

    @SerializedName("pageInfo")
    lateinit var pageInfo: PageInfo

    @SerializedName("items")
    lateinit var items: ArrayList<YoutubeChannelItem>
}
