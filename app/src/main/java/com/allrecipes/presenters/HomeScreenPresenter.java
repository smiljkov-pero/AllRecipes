package com.allrecipes.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.playlist.YoutubePlaylistsResponse;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.views.HomeScreenView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeScreenPresenter extends AbstractPresenter<HomeScreenView> {

    private static final String APP_LAST_CHANNEL_USED = "app.lastChannelUsed";
    public static final String TASTY_CHANNEL_ID_DEFAULT = "UCJFp8uSYCjXOMnkUyb3CQ3Q";

    private final GoogleYoutubeApiManager googleYoutubeApiManager;
    private final LocalStorageManagerInterface localStorageManagerInterface;

    private String pageToken;

    public HomeScreenPresenter(
            HomeScreenView view,
            GoogleYoutubeApiManager googleYoutubeApiManager,
            LocalStorageManagerInterface localStorageManagerInterface
    ) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
        this.localStorageManagerInterface = localStorageManagerInterface;
    }

    public void onChannelListClick(String channelId) {
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, channelId);
        fetchYoutubeChannelVideos(null, "");
    }

    public void fetchYoutubeChannelVideos(final String currentPageToken, String searchCriteria) {
        if (currentPageToken == null) {
            getView().showLoading();
        }
        String channelId = localStorageManagerInterface.getString(APP_LAST_CHANNEL_USED, "");
        if (TextUtils.isEmpty(channelId)) {
            channelId = TASTY_CHANNEL_ID_DEFAULT;
        }
        googleYoutubeApiManager.fetchChannelVideos(channelId, currentPageToken, 20, "date", searchCriteria)
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

    public void fetchPlaylistsAndVideos(String channelId) {
        googleYoutubeApiManager.fetchPlaylists(channelId, 50)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(new Function<YoutubePlaylistsResponse, ObservableSource<YoutubeChannelItem>>() {
                @Override
                public ObservableSource<YoutubeChannelItem> apply(@NonNull YoutubePlaylistsResponse youtubePlaylistsResponse) throws Exception {
                    return Observable.fromIterable(youtubePlaylistsResponse.getItems());
                }
            })
            .flatMap(new Function<YoutubeChannelItem, ObservableSource<YoutubePlaylistWithVideos>>() {
                @Override
                public ObservableSource<YoutubePlaylistWithVideos> apply(@NonNull YoutubeChannelItem youtubeChannelItem) throws Exception {
                    return getVideosForEachPlaylist(youtubeChannelItem);
                }
            }).subscribe(new Consumer<YoutubePlaylistWithVideos>() {
            @Override
            public void accept(@NonNull YoutubePlaylistWithVideos youtubePlaylistWithVideos) throws Exception {
                Log.d("playlist and videos", "playlist name: " + youtubePlaylistWithVideos.getChannel().getSnippet().title);
                Log.d("playlist and videos", "playlist videos size: " + youtubePlaylistWithVideos.getVideosResponse().items.size());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.e("playlist and videos", "error fetchPlaylistsAndVideos() " + throwable.getMessage());
            }
        });
    }

    public Observable<YoutubePlaylistWithVideos> getVideosForEachPlaylist(YoutubeChannelItem channel) {
        Observable<YoutubeVideoResponse> fetchVideosInPlaylistObservable = googleYoutubeApiManager.fetchVideosInPlaylist(channel.getId(), 50);
        return Observable.zip(Observable.just(channel), fetchVideosInPlaylistObservable,
            new BiFunction<YoutubeChannelItem, YoutubeVideoResponse, YoutubePlaylistWithVideos>() {
                @Override
                public YoutubePlaylistWithVideos apply(@NonNull YoutubeChannelItem youtubeChannelItem, @NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                    return new YoutubePlaylistWithVideos(youtubeChannelItem, youtubeVideoResponse);
                }
            });
    }
}
