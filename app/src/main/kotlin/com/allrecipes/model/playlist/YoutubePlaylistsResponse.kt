package com.allrecipes.model.playlist

import com.allrecipes.model.PageInfo
import com.google.gson.annotations.SerializedName

data class YoutubePlaylistsResponse (

    @SerializedName("pageInfo")
    var pageInfo: PageInfo? = null,

    @SerializedName("items")
    var items: ArrayList<YoutubeChannelItem>? = null
)
