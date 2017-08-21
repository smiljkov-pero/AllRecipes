package com.allrecipes.managers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.allrecipes.App;
import com.allrecipes.R;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class FavoritesManager {

    private static final String CHANNEL_FAVORITE_VIDEOS = "channel_favorite_videos_";

    private final LocalStorageManagerInterface localStorageManager;
    private final Context context;

    public FavoritesManager(LocalStorageManagerInterface localStorageManager, Context context) {
        this.localStorageManager = localStorageManager;
        this.context = context;
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
                SearchChannelVideosResponse youtubeVideoResponse = new SearchChannelVideosResponse();
                youtubeVideoResponse.items = youtubeItems;

                YoutubeChannelItem channelItem = new YoutubeChannelItem();
                YoutubeSnipped youtubeSnipped = new YoutubeSnipped();
                youtubeSnipped.title = context.getString(R.string.APP_FAVORITES);
                youtubeSnipped.channelId = channelId;
                youtubeSnipped.channelTitle = context.getString(R.string.APP_FAVORITES);
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
        YoutubeItem favoriteItem = findYoutubeItemInFavoriteList(item, favorites);
        favorites.remove(favoriteItem);
        String favoritesString = new Gson().toJson(favorites);
        localStorageManager.putString(CHANNEL_FAVORITE_VIDEOS + item.snippet.channelId, favoritesString);
    }

    @Nullable
    private YoutubeItem findYoutubeItemInFavoriteList(YoutubeItem item, List<YoutubeItem> favorites) {
        YoutubeItem favoriteItem = null;
        String itemVideoId = getItemVideoId(item);
        for (YoutubeItem favorite : favorites) {
            String favoriteVideoId = getItemVideoId(favorite);
            if (TextUtils.equals(itemVideoId, favoriteVideoId)) {
                favoriteItem = favorite;
                break;
            }
        }

        return favoriteItem;
    }

    @Nullable
    public YoutubeItem findItemInFavorites(YoutubeItem item) {
        return findYoutubeItemInFavoriteList(item, getChannelFavorites(item.snippet.channelId));
    }

    @android.support.annotation.NonNull
    private String getItemVideoId(YoutubeItem item) {
        String itemVideoId = "";
        if (item.id.videoId != null) {
            itemVideoId = item.id.videoId;
        } else if (item.snippet.resourceId.videoId != null) {
            itemVideoId = item.snippet.resourceId.videoId;
        }

        return itemVideoId;
    }
}
