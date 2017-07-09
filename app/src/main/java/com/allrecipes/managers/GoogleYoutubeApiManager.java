package com.allrecipes.managers;

import com.allrecipes.BuildConfig;
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
        int maxResults,
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

        return networkApi.fetchChannelVideos(BuildConfig.YOUTUBE_API_KEY, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<YoutubeVideoResponse> fetchVideo(String videoId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", videoId);
        params.put("part", "snippet,contentDetails,statistics");

        return networkApi.fetchVideo(BuildConfig.YOUTUBE_API_KEY, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<YoutubePlaylistsResponse> fetchPlaylists(String channelId, int maxResults) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("channelId", channelId);
        params.put("part", "snippet,contentDetails");
        params.put("maxResults", maxResults);

        return networkApi.fetchPlaylists(BuildConfig.YOUTUBE_API_KEY, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<YoutubeVideoResponse> fetchVideosInPlaylist(String playlistId, int maxResults) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("playlistId", playlistId);
        params.put("part", "snippet");
        params.put("maxResults", maxResults);

        return networkApi.fetchVideosInPlaylist(BuildConfig.YOUTUBE_API_KEY, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
