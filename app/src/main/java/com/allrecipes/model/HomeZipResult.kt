package com.allrecipes.model

import com.allrecipes.model.playlist.YoutubePlaylistWithVideos

class HomeZipResult {
    var videos: SearchChannelVideosResponse = SearchChannelVideosResponse()
    var swipeLanes: List<YoutubePlaylistWithVideos> = emptyList()
    var favoriteVideos: YoutubePlaylistWithVideos? = null
}