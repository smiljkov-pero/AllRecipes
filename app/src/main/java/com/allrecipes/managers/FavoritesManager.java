package com.allrecipes.managers;

import android.support.annotation.Nullable;

import com.allrecipes.model.YoutubeItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
