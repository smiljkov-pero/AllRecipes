package com.allrecipes.managers;

import com.allrecipes.BuildConfig;
import com.allrecipes.di.NetworkApi;
import com.allrecipes.model.SearchChannelVideosResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleYoutubeApiManager {

    private final NetworkApi networkApi;

    public GoogleYoutubeApiManager(NetworkApi networkApi) {
        this.networkApi = networkApi;
    }

    public Observable<SearchChannelVideosResponse> fetchChannelVideos(
        String channelId,
        String pageToken,
        int maxResults,
        String order
    ) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("channelId", channelId);
        params.put("maxResults", maxResults);
        params.put("part", "snippet,id");
        params.put("order", order);
        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }

        return networkApi.fetchChannelVideos(
            BuildConfig.YOUTUBE_API_KEY,
            params
        ).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }
}
