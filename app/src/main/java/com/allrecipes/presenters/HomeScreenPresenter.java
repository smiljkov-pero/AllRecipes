package com.allrecipes.presenters;

import android.text.TextUtils;

import com.allrecipes.di.managers.FirebaseDatabaseManager;
import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.model.Category;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.playlist.YoutubePlaylistsResponse;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.views.HomeScreenView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import rx.functions.Action1;

public class HomeScreenPresenter extends AbstractPresenter<HomeScreenView> {

    private static final String APP_LAST_CHANNEL_USED = "app.lastChannelUsed";
    private static final String APP_CACHED_FIREBASE_CONFIG = "app.cachedFirebaseConfig";
    private static final String TASTY_CHANNEL_ID_DEFAULT = "UCJFp8uSYCjXOMnkUyb3CQ3Q";

    private final GoogleYoutubeApiManager googleYoutubeApiManager;
    private final LocalStorageManagerInterface localStorageManagerInterface;
    private final FirebaseDatabaseManager firebaseDatabaseManager;

    Disposable fetchChannelVideosDisposable;
    Subscription getCategoriesConfigFromFirebaseSubscription;

    private Category category;
    private String pageToken;

    public HomeScreenPresenter(
        HomeScreenView view,
        GoogleYoutubeApiManager googleYoutubeApiManager,
        LocalStorageManagerInterface localStorageManagerInterface,
        FirebaseDatabaseManager firebaseDatabaseManager
    ) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
        this.localStorageManagerInterface = localStorageManagerInterface;
        this.firebaseDatabaseManager = firebaseDatabaseManager;
    }

    @Override
    public void unbindAll() {
        dispose(fetchChannelVideosDisposable);
        unsubscribe(getCategoriesConfigFromFirebaseSubscription);
    }

    public void onChannelListClick(Category category) {
        String categoryJson = new GsonBuilder().create().toJson(category, Category.class);
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, categoryJson);
        this.category = category;
        fetchYoutubeChannelVideos(null, "");
    }

    public void fetchYoutubeChannelVideos(final String currentPageToken, String searchCriteria) {
        if (currentPageToken == null) {
            getView().showLoading();
        }
        String channelId = category != null ? category.channelId : TASTY_CHANNEL_ID_DEFAULT;

        fetchChannelVideosDisposable = googleYoutubeApiManager
            .fetchChannelVideos(channelId, currentPageToken, 30, "date", searchCriteria)
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(@NonNull SearchChannelVideosResponse searchChannelVideosResponse) throws Exception {
                    if (isDisposedAndViewAvailable(fetchChannelVideosDisposable)) {
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
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    if (isDisposedAndViewAvailable(fetchChannelVideosDisposable)) {
                        getView().hideLoading();
                        throwable.printStackTrace();
                    }
                }
            });

    }

    public void onLoadMore(String searchCriteria) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria);
    }

    private void fetchPlayListsAndVideos(String channelId) {
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
            })/*.subscribe(new Consumer<YoutubePlaylistWithVideos>() {
            @Override
            public void accept(@NonNull YoutubePlaylistWithVideos youtubePlaylistWithVideos) throws Exception {
                Log.d("playlist and videos", "playlist name: " + youtubePlaylistWithVideos.getChannel().getSnippet().title);
                Log.d("playlist and videos", "playlist videos size: " + youtubePlaylistWithVideos.getVideosResponse().items.size());
                getView().addSwapLaneChannelItemToAdapter(youtubePlaylistWithVideos);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.e("playlist and videos", "error fetchPlayListsAndVideos() " + throwable.getMessage());
            }
        })*/;
    }

    private Observable<YoutubePlaylistWithVideos> getVideosForEachPlaylist(YoutubeChannelItem channel) {
        Observable<YoutubeVideoResponse> fetchVideosInPlaylistObservable = googleYoutubeApiManager.fetchVideosInPlaylist(channel.getId(), 50);
        return Observable.zip(Observable.just(channel), fetchVideosInPlaylistObservable,
            new BiFunction<YoutubeChannelItem, YoutubeVideoResponse, YoutubePlaylistWithVideos>() {
                @Override
                public YoutubePlaylistWithVideos apply(@NonNull YoutubeChannelItem youtubeChannelItem, @NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                    return new YoutubePlaylistWithVideos(youtubeChannelItem, youtubeVideoResponse);
                }
            });
    }

    public void onCreate() {
        String categoryJson = localStorageManagerInterface.getString(APP_LAST_CHANNEL_USED, "");
        category = new GsonBuilder().create().fromJson(categoryJson, Category.class);
        fetchYoutubeChannelVideos(null, "");
        getView().setToolbarTitleText(category != null ? category.getName() : "Tasty");
        String appConfig = localStorageManagerInterface.getString(APP_CACHED_FIREBASE_CONFIG, "");
        if (!TextUtils.isEmpty(appConfig)) {
            Type listType = new TypeToken<ArrayList<Category>>(){}.getType();
            List<Category> categories = new GsonBuilder().create().fromJson(appConfig, listType);
            getView().initChannelsListOverlayAdapter(categories, 0);
            fetchAppConfigFromFirebase(true);
        } else {
            fetchAppConfigFromFirebase(false);
        }
    }

    private void fetchAppConfigFromFirebase(final boolean isConfigAlreadyExist) {
        getCategoriesConfigFromFirebaseSubscription = firebaseDatabaseManager.getCategories()
                .subscribe(
            new Action1<List<Category>>() {
                @Override
                public void call(List<Category> categories) {
                    if (isSubscribedAndViewAvailable(getCategoriesConfigFromFirebaseSubscription)) {
                        if (!isConfigAlreadyExist) {
                            getView().initChannelsListOverlayAdapter(categories, 0);
                        }
                        for (Category category : categories) {
                            fetchPlayListsAndVideos(category.channelId);
                        }

                        localStorageManagerInterface.putString(
                            APP_CACHED_FIREBASE_CONFIG,
                            new GsonBuilder().create().toJson(categories)
                        );
                    }
                }
            },
            new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                }
            }
        );
    }
}
