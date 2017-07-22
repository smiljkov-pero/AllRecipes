package com.allrecipes.model

import com.google.gson.annotations.SerializedName

class RecommendedPlaylists {
    @SerializedName("visible") val visible: Boolean = true
    @SerializedName("channelId") val channelId: String = ""
}