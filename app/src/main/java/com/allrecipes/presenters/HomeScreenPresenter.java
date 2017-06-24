package com.allrecipes.presenters;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.ui.home.views.HomeScreenView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeScreenPresenter {

    protected WeakReference<HomeScreenView> view;
    private final GoogleYoutubeApiManager googleYoutubeApiManager;

    public HomeScreenPresenter(HomeScreenView view, GoogleYoutubeApiManager googleYoutubeApiManager) {
        this.view = new WeakReference(view);
        this.googleYoutubeApiManager = googleYoutubeApiManager;
    }

    public void fetchYoutubeChannelVideos() {
        googleYoutubeApiManager.fetchChannelVideos("UCJFp8uSYCjXOMnkUyb3CQ3Q", null, 30, "date")
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(@NonNull SearchChannelVideosResponse searchChannelVideosResponse) throws Exception {
                    List<YoutubeItem> items = searchChannelVideosResponse.items;
                    for (YoutubeItem item : items) {
                        view.get().addYoutubeItemToAdapter(item);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });

    }
}
