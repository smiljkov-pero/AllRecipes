package com.allrecipes.presenters;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.views.HomeScreenView;
import com.allrecipes.ui.videodetails.views.VideoDetailsView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VideoDetailsScreenPresenter extends AbstractPresenter<VideoDetailsView> {

    private final GoogleYoutubeApiManager googleYoutubeApiManager;

    Disposable fetchVideoDisposable;

    public VideoDetailsScreenPresenter(
        VideoDetailsView view,
        GoogleYoutubeApiManager googleYoutubeApiManager
    ) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
    }

    @Override
    public void unbindAll() {
        dispose(fetchVideoDisposable);
    }

    public void fetchVideo(String videoId) {
        getView().showLoading();
        fetchVideoDisposable = googleYoutubeApiManager.fetchVideo(videoId)
            .subscribe(new Consumer<YoutubeVideoResponse>() {
                @Override
                public void accept(@NonNull YoutubeVideoResponse response) throws Exception {
                    if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                        getView().hideLoading();
                        getView().setVideoDetails(response.items.get(0));
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                        getView().hideLoading();
                        throwable.printStackTrace();
                    }
                }
            });

    }
}
