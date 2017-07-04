package com.allrecipes.presenters;

import android.util.Log;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistsResponse;
import com.allrecipes.ui.home.views.HomeScreenView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeScreenPresenter extends AbstractPresenter<HomeScreenView> {

    private final GoogleYoutubeApiManager googleYoutubeApiManager;

    private String pageToken;

    public HomeScreenPresenter(HomeScreenView view, GoogleYoutubeApiManager googleYoutubeApiManager) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
    }

    public void fetchYoutubeChannelVideos(final String currentPageToken, String searchCriteria) {
        if (currentPageToken == null) {
            getView().showLoading();
        }
        googleYoutubeApiManager.fetchChannelVideos("UCJFp8uSYCjXOMnkUyb3CQ3Q", currentPageToken, 20, "date", searchCriteria)
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(@NonNull SearchChannelVideosResponse searchChannelVideosResponse) throws Exception {
                    if (currentPageToken == null) {
                        getView().clearAdapterItems();
                    }
                    getView().removeBottomListProgress();
                    List<YoutubeItem> items = searchChannelVideosResponse.items;
                    for (YoutubeItem item : items) {
                        getView().addYoutubeItemToAdapter(item);
                    }
                    pageToken = searchChannelVideosResponse.nextPageToken;
                    getView().hideLoading();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    getView().hideLoading();
                    throwable.printStackTrace();
                }
            });

    }

    public void onLoadMore(String searchCriteria) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria);
    }

    //TODO handle it properly
    public void fetchPlaylists(String channelId) {
        googleYoutubeApiManager.fetchPlaylists(channelId, 50)
            .subscribe(new Consumer<YoutubePlaylistsResponse>() {
            @Override
            public void accept(@NonNull YoutubePlaylistsResponse channels) throws Exception {
                for (YoutubeChannelItem channel: channels.getItems()) {
                    Log.d("channels", "channel info: name " +channel.getSnippet().title);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.d("channels", "channel error: " + throwable.getMessage());
            }
        });
    }
}
