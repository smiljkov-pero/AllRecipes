package com.allrecipes.di

import com.allrecipes.model.SearchChannelVideosResponse
import com.allrecipes.model.playlist.YoutubePlaylistsResponse
import com.allrecipes.model.video.YoutubeVideoResponse

import io.reactivex.Observable

import retrofit2.http.GET
import retrofit2.http.QueryMap
interface NetworkApi {

    companion object {
        val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    }

    @GET("search")
    fun fetchChannelVideos(@QueryMap queryParams: HashMap<String, Object>): Observable<SearchChannelVideosResponse>

    @GET("videos")
    fun fetchVideo(@QueryMap queryParams: HashMap<String, Object>): Observable<YoutubeVideoResponse>

    @GET("playlists")
    fun fetchPlaylists(@QueryMap queryParams: HashMap<String, Object>): Observable<YoutubePlaylistsResponse>

    @GET("playlistItems")
    fun fetchVideosInPlaylist(@QueryMap queryParams: HashMap<String, Object>): Observable<YoutubeVideoResponse>
}
