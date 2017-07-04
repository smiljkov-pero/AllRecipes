package com.allrecipes.model.playlist

import com.allrecipes.model.YoutubeSnipped
import com.google.gson.annotations.SerializedName

class YoutubeChannelItem {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("snippet")
    var snippet: YoutubeSnipped? = null
}
