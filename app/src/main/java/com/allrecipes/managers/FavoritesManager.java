package com.allrecipes.managers;

import android.support.annotation.Nullable;

import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class FavoritesManager {

    private static final String CHANNEL_FAVORITE_VIDEOS = "channel_favorite_videos_";

    private final LocalStorageManagerInterface localStorageManager;

    public FavoritesManager(LocalStorageManagerInterface localStorageManager) {
        this.localStorageManager = localStorageManager;
    }

    public List<YoutubeItem> getChannelFavorites(String channelId) {
        Type listType = new TypeToken<ArrayList<YoutubeItem>>(){}.getType();
        String favoriteVideos = localStorageManager.getString(CHANNEL_FAVORITE_VIDEOS + channelId, "");

        List<YoutubeItem> favorites = new Gson().fromJson(favoriteVideos, listType);

        return favorites != null ? favorites : new ArrayList<YoutubeItem>();
    }

    public Observable<YoutubePlaylistWithVideos> getFavoriteVideos(final String channelId) {
        return Observable.just(1).map(new Function<Object, YoutubePlaylistWithVideos>() {
            @Override
            public YoutubePlaylistWithVideos apply(@NonNull Object o) throws Exception {
                List<YoutubeItem> youtubeItems = getChannelFavorites(channelId);
                List<VideoItem> videoItems = new ArrayList<>();
                for (YoutubeItem youtubeItem: youtubeItems) {
                    VideoItem videoItem = new VideoItem();
                    videoItem.snippet = youtubeItem.snippet;
                    videoItem.id = youtubeItem.id.videoId;
                    videoItems.add(videoItem);
                }
                YoutubeVideoResponse youtubeVideoResponse = new YoutubeVideoResponse();
                youtubeVideoResponse.items = videoItems;

                YoutubeChannelItem channelItem = new YoutubeChannelItem();
                YoutubeSnipped youtubeSnipped = new YoutubeSnipped();
                youtubeSnipped.title = "Favorites";
                youtubeSnipped.channelId = channelId;
                youtubeSnipped.channelTitle = "Favorites";
                channelItem.setSnippet(youtubeSnipped);

                channelItem.setId(channelId);

                YoutubePlaylistWithVideos response = new YoutubePlaylistWithVideos(channelItem, youtubeVideoResponse, 0);
                return response;
            }
        });
    }

    public void putChannelFavoriteVideo(YoutubeItem item) {
        List<YoutubeItem> favorites = getChannelFavorites(item.snippet.channelId);
        favorites.add(0, item);
        String favoritesString = new Gson().toJson(favorites);
        localStorageManager.putString(CHANNEL_FAVORITE_VIDEOS + item.snippet.channelId, favoritesString);
    }

    public void removeChannelFavoriteVideo(YoutubeItem item) {
        List<YoutubeItem> favorites = getChannelFavorites(item.snippet.channelId);
        YoutubeItem favoriteItem = null;
        for (YoutubeItem favorite : favorites) {
            if (item.id.videoId.equals(favorite.id.videoId)) {
                favoriteItem = favorite;
                break;
            }
        }
        favorites.remove(favoriteItem);
        String favoritesString = new Gson().toJson(favorites);
        localStorageManager.putString(CHANNEL_FAVORITE_VIDEOS + item.snippet.channelId, favoritesString);
    }

    @Nullable
    public YoutubeItem findItemInFavorites(YoutubeItem item) {
        List<YoutubeItem> favorites = getChannelFavorites(item.snippet.channelId);
        YoutubeItem favoriteItem = null;
        for (YoutubeItem favorite : favorites) {
            if (item.id.videoId.equals(favorite.id.videoId)) {
                favoriteItem = favorite;
                break;
            }
        }

        return favoriteItem;
    }
}
