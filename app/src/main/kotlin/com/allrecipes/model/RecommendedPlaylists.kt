package com.allrecipes.model

import com.google.gson.annotations.SerializedName

data class RecommendedPlaylists (
    @SerializedName("visible") var visible: Boolean = true,
    @SerializedName("channelId") var channelId: String = "",
    @SerializedName("position") var position: Int = 0
)