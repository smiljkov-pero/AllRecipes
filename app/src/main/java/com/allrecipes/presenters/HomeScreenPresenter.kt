package com.allrecipes.presenters

import android.text.TextUtils
import android.util.Log
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.model.*
import com.allrecipes.model.playlist.YoutubeChannelItem
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.util.Constants
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class HomeScreenPresenter(
    view: WeakReference<HomeScreenView>,
    private val googleYoutubeApiManager: GoogleYoutubeApiManager,
    private val localStorageManagerInterface: LocalStorageManagerInterface,
    private val firebaseDatabaseManager: FirebaseDatabaseManager,
    private val remoteConfigManager: RemoteConfigManager
) : AbstractPresenter<HomeScreenView>(view) {

    private var fetchChannelVideosDisposable: Disposable? = null
    private val disposables = CompositeDisposable()

    private var currentChannel: Channel = Channel()
    private var pageToken: String? = null

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

    fun fetchYoutubeChannelVideos(
        currentPageToken: String?,
        searchCriteria: String?,
        currentFilterSettings: FiltersAndSortSettings
    ) {
        if (currentPageToken == null) {
            getView().showLoading()
        }
        if (TextUtils.isEmpty(searchCriteria) && TextUtils.isEmpty(currentPageToken)) {
            loadRecommendedPlayLists(currentChannel)
        }

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
                        {
                            fetchYoutubeChannelVideos(currentPageToken,
                                                      searchCriteria,
                                                      currentFilterSettings)
                        }
                    )
                }

            }

    }

    fun onLoadMore(searchCriteria: String?, currentFilterSettings: FiltersAndSortSettings) {
        fetchYoutubeChannelVideos(pageToken, searchCriteria, currentFilterSettings)
    }

    private fun fetchVideosFromPlaylist(channelName: String, recommendedPlaylists: RecommendedPlaylists) {
        val d = googleYoutubeApiManager.fetchVideosInPlaylist(
            recommendedPlaylists.channelId,
            remoteConfigManager.videoListItemsPerPage, null
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ youtubeVideoResponse ->
                           val channelItem = YoutubeChannelItem()
                           val youtubeSnipped = YoutubeSnipped()
                           youtubeSnipped.title = channelName
                           youtubeSnipped.channelId = recommendedPlaylists.channelId
                           youtubeSnipped.channelTitle = channelName
                           channelItem.snippet = youtubeSnipped
                           getView().addSwapLaneChannelItemToAdapter(YoutubePlaylistWithVideos(
                               channelItem,
                               youtubeVideoResponse
                           ), recommendedPlaylists.position)
                       }) { throwable -> Log.e("fetchVideosFromPlaylist", "", throwable) }
        disposables.add(d)
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

    private fun loadRecommendedPlayLists(channel: Channel) {
        val recommendedPlayLists = channel.recommendedPlaylists
        for ((key, value) in recommendedPlayLists) {
            if (value.visible) {
                fetchVideosFromPlaylist(key, value)
            }
        }
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
}
