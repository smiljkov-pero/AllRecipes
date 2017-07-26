package com.allrecipes.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.allrecipes.di.managers.FirebaseDatabaseManager;
import com.allrecipes.managers.GoogleYoutubeApiManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.managers.remoteconfig.RemoteConfigManager;
import com.allrecipes.model.DefaultChannel;
import com.allrecipes.model.Channel;
import com.allrecipes.model.FiltersAndSortSettings;
import com.allrecipes.model.RecipeFilterOption;
import com.allrecipes.model.RecommendedPlaylists;
import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.playlist.YoutubeChannelItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import rx.functions.Action1;

public class HomeScreenPresenter extends AbstractPresenter<HomeScreenView> {

    private static final String APP_LAST_CHANNEL_USED = "app.lastChannelUsed";

    private final GoogleYoutubeApiManager googleYoutubeApiManager;
    private final LocalStorageManagerInterface localStorageManagerInterface;
    private final FirebaseDatabaseManager firebaseDatabaseManager;
    private final RemoteConfigManager remoteConfigManager;

    private Disposable fetchChannelVideosDisposable;
    private CompositeDisposable disposables = new CompositeDisposable();

    private Channel currentChannel;
    private String pageToken;
    private String oAuthToken;

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
        disposables.clear();
    }

    public void onChannelListClick(Channel channel, FiltersAndSortSettings currentFilterSettings) {
        saveLastUsedChannel(channel);
        this.currentChannel = channel;
        fetchYoutubeChannelVideos(null, "", currentFilterSettings);
    }

    private void saveLastUsedChannel(Channel channel) {
        String categoryJson = new GsonBuilder().create().toJson(channel, Channel.class);
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, categoryJson);
    }

    private String constructSearchFromFilters(String searchCriteria, List<RecipeFilterOption> filters) {
        StringBuilder filtersCombined = new StringBuilder();

        if (!TextUtils.isEmpty(searchCriteria)) {
            filtersCombined.append(searchCriteria);
            if (!filters.isEmpty()) {
                filtersCombined.append("&");
            }
        }
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).isChecked()) {
                filtersCombined.append(filters.get(i).getRecipeFilter());
                if (i < filters.size() - 1) {
                    filtersCombined.append("|");
                }
            }
        }

        return filtersCombined.toString();
    }

    public void fetchYoutubeChannelVideos(
        final String currentPageToken,
        String searchCriteria,
        FiltersAndSortSettings currentFilterSettings
    ) {
        if (currentPageToken == null) {
            getView().showLoading();
        }
        if (TextUtils.isEmpty(searchCriteria) && TextUtils.isEmpty(currentPageToken)) {
            loadRecommendedPlayLists(currentChannel);
        }

        fetchChannelVideosDisposable = googleYoutubeApiManager
            .fetchChannelVideos(
                currentChannel.getChannelId(),
                currentPageToken,
                remoteConfigManager.getVideoListItemsPerPage(),
                currentFilterSettings.getSort(),
                constructSearchFromFilters(searchCriteria, currentFilterSettings.getFilters()),
                oAuthToken
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
                        int position = 0;
                        for (YoutubeItem item : items) {
                            getView().addYoutubeItemToAdapter(item, position);
                            position++;
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

    public void onLoadMore(String searchCriteria, FiltersAndSortSettings currentFilterSettings) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria, currentFilterSettings);
    }

    private void fetchVideosFromPlaylist(final String channelName,final RecommendedPlaylists recommendedPlaylists) {
       Disposable d = googleYoutubeApiManager.fetchVideosInPlaylist(recommendedPlaylists.getChannelId(),
                                                                    remoteConfigManager.getVideoListItemsPerPage()
           , null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<YoutubeVideoResponse>() {
                    @Override
                    public void accept(@NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                        YoutubeChannelItem channelItem = new YoutubeChannelItem();
                        YoutubeSnipped youtubeSnipped = new YoutubeSnipped();
                        youtubeSnipped.title = channelName;
                        youtubeSnipped.channelId = recommendedPlaylists.getChannelId();
                        youtubeSnipped.channelTitle = channelName;
                        channelItem.setSnippet(youtubeSnipped);
                        getView().addSwapLaneChannelItemToAdapter(new YoutubePlaylistWithVideos(channelItem, youtubeVideoResponse),recommendedPlaylists.getPosition());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e("fetchVideosFromPlaylist","",throwable);
                    }
                });
        disposables.add(d);
    }

    private Observable<YoutubePlaylistWithVideos> getVideosForEachPlaylist(YoutubeChannelItem channel) {
        Observable<YoutubeVideoResponse> fetchVideosInPlaylistObservable = googleYoutubeApiManager.fetchVideosInPlaylist(channel.getId(), 50, null);
        return Observable.zip(Observable.just(channel), fetchVideosInPlaylistObservable,
            new BiFunction<YoutubeChannelItem, YoutubeVideoResponse, YoutubePlaylistWithVideos>() {
                @Override
                public YoutubePlaylistWithVideos apply(@NonNull YoutubeChannelItem youtubeChannelItem, @NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                    return new YoutubePlaylistWithVideos(youtubeChannelItem, youtubeVideoResponse);
                }
            });
    }

    public void onCreate(String oAuthToken) {
        this.oAuthToken = oAuthToken;

        String lastUsedChannel = localStorageManagerInterface.getString(APP_LAST_CHANNEL_USED, "");
        currentChannel = new GsonBuilder().create().fromJson(lastUsedChannel, Channel.class);

        getView().setToolbarTitleText(currentChannel.getName());
        FiltersAndSortSettings filtersAndSortSettings = initDefaultFilterAndSortSettings();
        fetchYoutubeChannelVideos(null, "", filtersAndSortSettings);

        getView().initChannelsListOverlayAdapter(firebaseDatabaseManager.restoreFirebaseConfig(), 0);
    }

    private void loadRecommendedPlayLists(Channel channel) {
        Map<String, RecommendedPlaylists> recommendedPlayLists = channel.getRecommendedPlaylists();
        for (Map.Entry<String,RecommendedPlaylists> recommended : recommendedPlayLists.entrySet()) {
            if (recommended.getValue().getVisible()) {
                fetchVideosFromPlaylist(recommended.getKey(), recommended.getValue());
            }
        }
    }

    private FiltersAndSortSettings initDefaultFilterAndSortSettings() {
        FiltersAndSortSettings filtersAndSortSettings = new FiltersAndSortSettings();
        filtersAndSortSettings.setSort(remoteConfigManager.getDefaultFilterSort());
        List<String> remoteFilterCategories = remoteConfigManager.getFilterCategories();
        if (remoteFilterCategories != null) {
            for (String filterCategory : remoteFilterCategories) {
                filtersAndSortSettings.getFilters().add(new RecipeFilterOption(filterCategory, false));
            }
        }
        getView().setCurrentFilterSettings(filtersAndSortSettings);

        return filtersAndSortSettings;
    }

    public void fetchMoreVideosFromPlaylist(SwipeLaneChannelItem item) {
        final WeakReference<SwipeLaneChannelItem> weakViewReference = new WeakReference<>(item);
        Disposable d = googleYoutubeApiManager.fetchVideosInPlaylist(item.getItem().getChannel().getSnippet().channelId,
                                                                     remoteConfigManager.getVideoListItemsPerPage(),
                                                                     item.getItem().getVideosResponse().nextPageToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<YoutubeVideoResponse>() {
                @Override
                public void accept(@NonNull YoutubeVideoResponse youtubeVideoResponse) throws Exception {
                    SwipeLaneChannelItem view = weakViewReference.get();
                    if (view != null) {
                        view.loadMore(youtubeVideoResponse);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    Log.e("fetchVideosFromPlaylist", "", throwable);
                }
            });
        disposables.add(d);
    }
}
