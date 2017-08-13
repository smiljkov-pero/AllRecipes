package com.allrecipes.model

import com.google.gson.annotations.SerializedName

data class RecommendedPlaylists (
    @SerializedName("visible") var visible: Boolean = true,
    @SerializedName("playlistId") var playlistId: String = "",
    @SerializedName("position") var position: Int = 0
)