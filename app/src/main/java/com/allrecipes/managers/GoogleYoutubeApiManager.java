package com.allrecipes.managers;

import com.allrecipes.di.NetworkApi;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.playlist.YoutubePlaylistsResponse;
import com.allrecipes.model.video.YoutubeVideoResponse;

import java.util.HashMap;

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
        long maxResults,
        String order,
        String searchCriteria
    ) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("channelId", channelId);
        params.put("maxResults", maxResults);
        params.put("part", "snippet,id");
        params.put("order", order);

        if (searchCriteria.length() > 0) {
            params.put("q", searchCriteria);
        }
        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }

        return networkApi.fetchChannelVideos(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<YoutubeVideoResponse> fetchVideo(String videoId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", videoId);
        params.put("part", "snippet,contentDetails,statistics");

        return networkApi.fetchVideo(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SearchChannelVideosResponse> fetchVideosInPlaylist(String playlistId, long maxResults, String pageToken) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("playlistId", playlistId);
        params.put("part", "snippet,id");
        params.put("maxResults", maxResults);

        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }

        return networkApi.fetchVideosInPlaylist(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
