package com.allrecipes.model

import com.google.gson.annotations.SerializedName

import java.util.HashMap

class Channel {

    @SerializedName("name")
    var name: String = ""
    @SerializedName("image")
    var image: String = ""
    @SerializedName("youtubeUrl")
    var youtubeUrl: String = ""
    @SerializedName("channelId")
    var channelId: String = ""
    @SerializedName("description")
    var description: String = ""
    @SerializedName("recommendedPlaylists")
    var recommendedPlaylists: Map<String, RecommendedPlaylists> = HashMap()
}
