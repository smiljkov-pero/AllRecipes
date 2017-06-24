package com.allrecipes.presenters;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.Youtube;
import com.allrecipes.ui.home.views.HomeScreenView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeScreenPresenter extends AbstractPresenter<HomeScreenView> {

    private final GoogleYoutubeApiManager googleYoutubeApiManager;

    public HomeScreenPresenter(HomeScreenView view, GoogleYoutubeApiManager googleYoutubeApiManager) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
    }

    public void fetchYoutubeChannelVideos() {
        getView().showLoading();
        googleYoutubeApiManager.fetchChannelVideos("UCJFp8uSYCjXOMnkUyb3CQ3Q", null, 30, "date")
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(@NonNull SearchChannelVideosResponse searchChannelVideosResponse) throws Exception {
                    List<Youtube> items = searchChannelVideosResponse.items;
                    for (Youtube item : items) {
                        getView().addYoutubeItemToAdapter(item);
                    }
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
}
