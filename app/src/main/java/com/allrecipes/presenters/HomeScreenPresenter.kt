package com.allrecipes.presenters

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.allrecipes.managers.FavoritesManager
import com.allrecipes.managers.FirebaseDatabaseManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.model.*
import com.allrecipes.model.playlist.YoutubeChannelItem
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos
import com.allrecipes.tracking.providers.firebase.FirebaseTracker
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.util.Constants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class HomeScreenPresenter(
    view: WeakReference<HomeScreenView>,
    private val googleYoutubeApiManager: GoogleYoutubeApiManager,
    private val localStorageManagerInterface: LocalStorageManagerInterface,
    private val firebaseDatabaseManager: FirebaseDatabaseManager,
    private val remoteConfigManager: RemoteConfigManager,
    private val firebaseTracker: FirebaseTracker,
    private val favoritesManager: FavoritesManager
) : AbstractPresenter<HomeScreenView>(view) {

    private var fetchChannelVideosDisposable: Disposable? = null
    private val disposables = CompositeDisposable()

    private var currentChannel: Channel = Channel()
    private var pageToken: String? = null
    private var favoritesCount = 0

    override fun unbindAll() {
        dispose(fetchChannelVideosDisposable)
        disposables.clear()
    }

    fun onChannelListClick(channel: Channel, currentFilterSettings: FiltersAndSortSettings) {
        saveLastUsedChannel(channel)
        this.currentChannel = channel
        fetchYoutubeChannelVideos(null, "", currentFilterSettings)
    }

    private fun saveLastUsedChannel(channel: Channel) {
        val categoryJson = GsonBuilder().create().toJson(channel, Channel::class.java)
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, categoryJson)
    }

    private fun constructSearchFromFilters(searchCriteria: String?, filters: List<RecipeFilterOption>): String {
        val filtersCombined = StringBuilder()

        if (!TextUtils.isEmpty(searchCriteria)) {
            filtersCombined.append(searchCriteria)
            if (!filters.isEmpty()) {
                filtersCombined.append("&")
            }
        }
        for (i in filters.indices) {
            if (filters[i].isChecked) {
                filtersCombined.append(filters[i].recipeFilter)
                if (i < filters.size - 1) {
                    filtersCombined.append("|")
                }
            }
        }
        if (filtersCombined.contains("|")) {
            filtersCombined.replace(filtersCombined.length - 1, filtersCombined.length, "")
        }

        return filtersCombined.toString()
    }

    fun trackVideoSearch(searchCriteria: String?, sort: String, filters: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchCriteria)
        bundle.putString("filters", filters)
        bundle.putString("sort", sort)
        bundle.putString("channel_id", currentChannel.channelId)
        bundle.putString("channel_name", currentChannel.name)

        firebaseTracker.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    fun fetchYoutubeChannelVideos(
        currentPageToken: String?,
        searchCriteria: String?,
        currentFilterSettings: FiltersAndSortSettings
    ) {
        if (currentPageToken == null) {
            getView().showLoading()
            trackVideoSearch(searchCriteria, currentFilterSettings.sort, currentFilterSettings.filters.joinToString())
        }

        if (TextUtils.isEmpty(currentPageToken) && TextUtils.isEmpty(searchCriteria) && !currentFilterSettings.isAtLeastOneFilterSet ) {
            fetchHomeYoutubeVideos(currentPageToken, searchCriteria, currentFilterSettings)
        } else {
            searchYoutubeVideos(currentPageToken, currentFilterSettings, searchCriteria)
        }
    }

    private fun fetchHomeYoutubeVideos(
        currentPageToken: String?,
        searchCriteria: String?,
        currentFilterSettings: FiltersAndSortSettings
    ) {
        val videosObservable: Observable<SearchChannelVideosResponse> = googleYoutubeApiManager
            .fetchChannelVideos(
                currentChannel.channelId,
                currentPageToken,
                remoteConfigManager.videoListItemsPerPage,
                currentFilterSettings.sort,
                constructSearchFromFilters(searchCriteria, currentFilterSettings.filters))

        val swipeLanesObservable: Observable<List<YoutubePlaylistWithVideos>> = loadRecommendedPlayListsZip(currentChannel)
        val favoritesObservable: Observable<YoutubePlaylistWithVideos> = favoritesManager.getFavoriteVideos(currentChannel.channelId)

        Observable.zip(videosObservable, swipeLanesObservable, favoritesObservable,
                       Function3 { youtubeVideos: SearchChannelVideosResponse,
                                   swipeLanes: List<YoutubePlaylistWithVideos>,
                                   favoriteVideos: YoutubePlaylistWithVideos ->
                           val homeZipItems: HomeZipResult = HomeZipResult()
                           homeZipItems.videos = youtubeVideos
                           homeZipItems.swipeLanes = swipeLanes
                           homeZipItems.favoriteVideos = favoriteVideos
                           favoritesCount = favoriteVideos?.videosResponse?.items?.size!!
                           homeZipItems
                       })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ homeZip ->
                           if (isViewAvailable) {
                               getView().hideLoading()

                               if (currentPageToken == null) {
                                   getView().clearAdapterItems()
                               }
                               getView().removeBottomListProgress()
                               val items = homeZip.videos.items
                               var position = 0
                               items.forEach {
                                   getView().addYoutubeItemToAdapter(it, position)
                                   position++
                               }
                               pageToken = homeZip.videos.nextPageToken

                               homeZip.swipeLanes.forEach {
                                   getView().addSwapLaneChannelItemToAdapter(it, it.position)
                               }

                               val userHasFavVideos: Boolean? = homeZip.favoriteVideos?.videosResponse?.items?.isEmpty()
                               if (userHasFavVideos != null && userHasFavVideos == false) {
                                   getView().addSwapLaneChannelItemToAdapter(homeZip.favoriteVideos!!, 0)
                               }
                           }
                       }, { t ->
                           if (isViewAvailable) {
                               getView().hideLoading()
                               t.printStackTrace()
                               getView().handleApiError(
                                   t, {
                                   fetchYoutubeChannelVideos(
                                       currentPageToken,
                                       searchCriteria,
                                       currentFilterSettings
                                   )
                               })
                           }
                       })
    }

    private fun searchYoutubeVideos(
        currentPageToken: String?,
        currentFilterSettings: FiltersAndSortSettings,
        searchCriteria: String?
    ) {
        fetchChannelVideosDisposable = googleYoutubeApiManager
            .fetchChannelVideos(
                currentChannel.channelId,
                currentPageToken,
                remoteConfigManager.videoListItemsPerPage,
                currentFilterSettings.sort,
                constructSearchFromFilters(searchCriteria, currentFilterSettings.filters)
            )
            .subscribe({ searchChannelVideosResponse ->
                       if (isViewAvailable) {
                           if (currentPageToken == null) {
                               getView().clearAdapterItems()
                           }
                           getView().removeBottomListProgress()
                           val items = searchChannelVideosResponse.items
                           var position = 0
                           items.forEach {
                               getView().addYoutubeItemToAdapter(it, position)
                               position++
                           }
                           pageToken = searchChannelVideosResponse.nextPageToken
                           getView().hideLoading()
                       }
                   }) { throwable ->
                if (isViewAvailable) {
                    getView().hideLoading()
                    throwable.printStackTrace()
                    getView().handleApiError(
                        throwable,
                        { fetchYoutubeChannelVideos(currentPageToken, searchCriteria, currentFilterSettings) }
                    )
                }
            }
    }

    fun onLoadMore(searchCriteria: String?, currentFilterSettings: FiltersAndSortSettings) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria, currentFilterSettings)
    }

    private fun getSwipeLaneChannelSubscription(channelName: String, recommendedPlaylists: RecommendedPlaylists)
        : Observable<YoutubePlaylistWithVideos> {
        val s: Observable<YoutubePlaylistWithVideos> = googleYoutubeApiManager.fetchVideosInPlaylist(
            recommendedPlaylists.playlistId,
            remoteConfigManager.videoListItemsPerPage, null
        )
            .flatMap({ youtubeVideoResponse ->
                         val channelItem = YoutubeChannelItem()
                         val youtubeSnipped = YoutubeSnipped()
                         youtubeSnipped.title = channelName
                         youtubeSnipped.channelId = recommendedPlaylists.playlistId
                         youtubeSnipped.channelTitle = channelName
                         channelItem.snippet = youtubeSnipped
                         val response = YoutubePlaylistWithVideos(
                             channelItem,
                             youtubeVideoResponse,
                             recommendedPlaylists.position
                         )
                         Observable.just(response)
                     })

        return s
    }


    fun onCreate(oAuthToken: String?) {
        if (!TextUtils.isEmpty(oAuthToken)) {
            localStorageManagerInterface.putString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, oAuthToken)
        }

        val lastUsedChannel = localStorageManagerInterface.getString(APP_LAST_CHANNEL_USED, "")
        currentChannel = GsonBuilder().create().fromJson(lastUsedChannel, Channel::class.java)

        getView().setToolbarTitleText(currentChannel.name)
        val filtersAndSortSettings = initDefaultFilterAndSortSettings()
        fetchYoutubeChannelVideos(null, "", filtersAndSortSettings)

        getView().initChannelsListOverlayAdapter(firebaseDatabaseManager.restoreFirebaseConfig(), 0)
    }

    private fun loadRecommendedPlayListsZip(channel: Channel): Observable<List<YoutubePlaylistWithVideos>> {
        val swipeLanesObservables: ArrayList<Observable<YoutubePlaylistWithVideos>> = ArrayList()
        val recommendedPlayLists = channel.recommendedPlaylists
        for ((key, value) in recommendedPlayLists) {
            if (value.visible) {
                val observable: Observable<YoutubePlaylistWithVideos> = getSwipeLaneChannelSubscription(key, value)
                swipeLanesObservables.add(observable)
            }
        }

        if (swipeLanesObservables.isEmpty()) {
            return Observable.just(emptyList())
        }

        return Observable.zip(swipeLanesObservables, {
            t: Array<out Any> ->
            t.toList() as List<YoutubePlaylistWithVideos>
        })
    }

    private fun initDefaultFilterAndSortSettings(): FiltersAndSortSettings {
        val filtersAndSortSettings = FiltersAndSortSettings()
        filtersAndSortSettings.sort = remoteConfigManager.defaultFilterSort
        val remoteFilterCategories = remoteConfigManager.filterCategories
        if (remoteFilterCategories != null) {
            for (filterCategory in remoteFilterCategories) {
                filtersAndSortSettings.filters.add(RecipeFilterOption(filterCategory, false))
            }
        }
        getView().setCurrentFilterSettings(filtersAndSortSettings)

        return filtersAndSortSettings
    }

    fun fetchMoreVideosFromPlaylist(item: SwipeLaneChannelItem) {
        val weakViewReference = WeakReference(item)
        val d = googleYoutubeApiManager.fetchVideosInPlaylist(
            item.item.channel.snippet!!.channelId,
            remoteConfigManager.videoListItemsPerPage,
            item.item.videosResponse.nextPageToken
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ youtubeVideoResponse ->
                           val view = weakViewReference.get()
                           view?.loadMore(youtubeVideoResponse)
                       }) { throwable -> Log.e("fetchVideosFromPlaylist", "", throwable) }
        disposables.add(d)
    }

    companion object {
        private val APP_LAST_CHANNEL_USED = "app.lastChannelUsed"
    }

    fun onResume(currentFilterSettings: FiltersAndSortSettings) {
        if (favoritesCount != favoritesManager.getChannelFavorites(currentChannel.channelId).size) {
            fetchYoutubeChannelVideos(null, "", currentFilterSettings)
        }
    }
}
