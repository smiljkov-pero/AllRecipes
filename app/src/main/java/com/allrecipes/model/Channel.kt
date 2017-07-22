package com.allrecipes.model

import com.google.gson.annotations.SerializedName

class Channel {

    @SerializedName("name") val name: String = ""
    @SerializedName("image") val image: String = ""
    @SerializedName("youtubeUrl") val youtubeUrl: String = ""
    @SerializedName("channelId") val channelId: String = ""
    @SerializedName("description") val description: String = ""
    @SerializedName("recommendedPlaylists") val recommendedPlaylists: Map<String, RecommendedPlaylists> = HashMap()
}
