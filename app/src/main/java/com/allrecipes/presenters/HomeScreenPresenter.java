package com.allrecipes.presenters;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeScreenPresenter {

    private final GoogleYoutubeApiManager googleYoutubeApiManager;

    public HomeScreenPresenter(GoogleYoutubeApiManager googleYoutubeApiManager) {
        this.googleYoutubeApiManager = googleYoutubeApiManager;
    }

    public void fetchYoutubeChannelVideos() {
        googleYoutubeApiManager.fetchChannelVideos("UCJFp8uSYCjXOMnkUyb3CQ3Q", null, 30, "date")
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(@NonNull SearchChannelVideosResponse searchChannelVideosResponse) throws Exception {
                    List<YoutubeItem> items = searchChannelVideosResponse.items;
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });

    }
}
