package com.allrecipes.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.allrecipes.di.managers.FirebaseDatabaseManager;
import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.managers.remoteconfig.RemoteConfigManager;
import com.allrecipes.model.DefaultChannel;
import com.allrecipes.model.Channel;
import com.allrecipes.model.RecommendedPlaylists;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.playlist.YoutubePlaylistsResponse;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.views.HomeScreenView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private final GoogleYoutubeApiManager googleYoutubeApiManager;
    private final LocalStorageManagerInterface localStorageManagerInterface;
    private final FirebaseDatabaseManager firebaseDatabaseManager;
    private final RemoteConfigManager remoteConfigManager;

    private Disposable fetchChannelVideosDisposable;
    Subscription getCategoriesConfigFromFirebaseSubscription;

    private Channel channel;
    private String pageToken;

    public HomeScreenPresenter(
        HomeScreenView view,
        GoogleYoutubeApiManager googleYoutubeApiManager,
        LocalStorageManagerInterface localStorageManagerInterface,
        FirebaseDatabaseManager firebaseDatabaseManager,
        RemoteConfigManager remoteConfigManager
    ) {
        super(new WeakReference<>(view));
        this.googleYoutubeApiManager = googleYoutubeApiManager;
        this.localStorageManagerInterface = localStorageManagerInterface;
        this.firebaseDatabaseManager = firebaseDatabaseManager;
        this.remoteConfigManager = remoteConfigManager;
    }

    @Override
    public void unbindAll() {
        dispose(fetchChannelVideosDisposable);
        unsubscribe(getCategoriesConfigFromFirebaseSubscription);
    }

    public void onChannelListClick(Channel channel, String sortBy) {
        String categoryJson = new GsonBuilder().create().toJson(channel, Channel.class);
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, categoryJson);
        this.channel = channel;
        fetchYoutubeChannelVideos(null, "", sortBy);
    }

    public void fetchYoutubeChannelVideos(
        final String currentPageToken,
        String searchCriteria,
        String sortBy
    ) {
        if (currentPageToken == null) {
            getView().showLoading();
        }
        if (TextUtils.isEmpty(searchCriteria)) {
            loadRecommendedPlayLists(channel);
        }

        fetchChannelVideosDisposable = googleYoutubeApiManager
            .fetchChannelVideos(
                channel.getChannelId(),
                currentPageToken,
                remoteConfigManager.getVideoListItemsPerPage(),
                sortBy,
                searchCriteria
            )
            .subscribe(new Consumer<SearchChannelVideosResponse>() {
                @Override
                public void accept(
                    @NonNull SearchChannelVideosResponse searchChannelVideosResponse
                ) throws Exception {
                    if (isViewAvailable()) {
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
                    if (isViewAvailable()) {
                        getView().hideLoading();
                        throwable.printStackTrace();
                    }
                }
            });

    }

    public void onLoadMore(String searchCriteria, String sortBy) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria, sortBy);
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
            }).subscribe(new Consumer<YoutubePlaylistWithVideos>() {
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
        });
    }

    private void fetchVideosFromPlaylist(final String channelId, final String channelName) {
        googleYoutubeApiManager.fetchVideosInPlaylist(channelId, 50)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<YoutubeVideoResponse>() {
                    @Override
                    public void accept(@NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                        YoutubeChannelItem channelItem = new YoutubeChannelItem();
                        YoutubeSnipped youtubeSnipped = new YoutubeSnipped();
                        youtubeSnipped.title = channelName;
                        youtubeSnipped.channelId = channelId;
                        youtubeSnipped.channelTitle = channelName;
                        channelItem.setSnippet(youtubeSnipped);
                        getView().addSwapLaneChannelItemToAdapter(new YoutubePlaylistWithVideos(channelItem, youtubeVideoResponse));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e("fetchVideosFromPlaylist","",throwable);
                    }
                });
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

    public void onCreate(final String sortBy) {
        if (remoteConfigManager.isRemoteConfigNotFetchYet()) {
            remoteConfigManager.reload(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        remoteConfigManager.activateFetched();
                        initCurrentChannel(sortBy);
                    }
                }
            });
        } else {
            initCurrentChannel(sortBy);
        }
        initFirebaseDBConfig();
    }

    private void initFirebaseDBConfig() {
        String appConfig = localStorageManagerInterface.getString(APP_CACHED_FIREBASE_CONFIG, "");

        if (!TextUtils.isEmpty(appConfig)) {
            Type listType = new TypeToken<ArrayList<Channel>>(){}.getType();
            List<Channel> categories = new GsonBuilder().create().fromJson(appConfig, listType);
            getView().initChannelsListOverlayAdapter(categories, 0);
            fetchAppConfigFromFirebase(true);
        } else {
            fetchAppConfigFromFirebase(false);
        }
    }

    private void initCurrentChannel(String sortBy) {
        String lastUsedChannel = localStorageManagerInterface.getString(APP_LAST_CHANNEL_USED, "");
        channel = new GsonBuilder().create().fromJson(lastUsedChannel, Channel.class);
        if (channel == null) {
            String remoteConfigString = remoteConfigManager.getDefaultChannel();
            channel = new GsonBuilder().create()
                .fromJson(remoteConfigString, DefaultChannel.class).defaultChannel;
        }

        getView().setToolbarTitleText(channel.getName());
        fetchYoutubeChannelVideos(null, "", sortBy);
    }

    private void loadRecommendedPlayLists(Channel channel){
        Map<String, RecommendedPlaylists> recommendedPlayLists = channel.getRecommendedPlayLists();
        for (Map.Entry<String,RecommendedPlaylists> recommended : recommendedPlayLists.entrySet()) {
            if (recommended.getValue().getVisible()) {
                fetchVideosFromPlaylist(recommended.getValue().getChannelId(), recommended.getKey());
            }
        }
    }

    private void fetchAppConfigFromFirebase(final boolean isConfigAlreadyExist) {
        getCategoriesConfigFromFirebaseSubscription = firebaseDatabaseManager.getCategories()
                .subscribe(
            new Action1<List<Channel>>() {
                @Override
                public void call(List<Channel> categories) {
                    if (isSubscribedAndViewAvailable(getCategoriesConfigFromFirebaseSubscription)) {
                        if (!isConfigAlreadyExist) {
                            getView().initChannelsListOverlayAdapter(categories, 0);
                        }
                        /*for (Channel channel : categories) {
                            fetchPlayListsAndVideos(channel.channelId);
                        }*/
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
                    Log.e("getting_app_config", "", throwable);
                }
            }
        );
    }
}
