package com.allrecipes.di

import com.allrecipes.model.SearchChannelVideosResponse

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NetworkApi {

    @GET("search")
    fun fetchChannelVideos(
        @Query("key") key: String,
        @QueryMap queryParams: HashMap<String, Object>
    ): Observable<SearchChannelVideosResponse>

    companion object {
        val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    }
}
