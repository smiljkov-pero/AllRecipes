package com.allrecipes.model.playlist

import com.allrecipes.model.YoutubeSnipped
import com.google.gson.annotations.SerializedName

class YoutubeChannelItem {
    @SerializedName("id")
    lateinit var id: String

    @SerializedName("snippet")
    lateinit var snippet: YoutubeSnipped
}
