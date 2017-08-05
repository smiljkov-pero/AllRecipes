package com.allrecipes.presenters

import com.allrecipes.managers.FavoritesManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.model.SearchChannelVideosResponse
import com.allrecipes.model.YoutubeItem
import com.allrecipes.model.video.YoutubeVideoResponse
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.videodetails.views.VideoDetailsView

import java.lang.ref.WeakReference

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class VideoDetailsScreenPresenter(
    view: WeakReference<VideoDetailsView>,
    private val googleYoutubeApiManager: GoogleYoutubeApiManager,
    private val remoteConfigManager: RemoteConfigManager,
    private val favoritesManager: FavoritesManager
) : AbstractPresenter<VideoDetailsView>(view) {

    private var fetchVideoDisposable: Disposable? = null

    override fun unbindAll() {
        dispose(fetchVideoDisposable)
    }

    fun fetchVideo(video: YoutubeItem) {
        getView().showLoading()
        val id: String
        if (video.id != null && video.id.videoId != null)
            id = video.id.videoId
        else
            id = video.snippet.resourceId.videoId
        fetchVideoDisposable = googleYoutubeApiManager.fetchVideo(id)
            .subscribe({ response ->
               if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                   getView().hideLoading()
                   getView().setVideoDetails(response.items[0], isSavedAsFavorite(video))
               }
            }) { throwable ->
                if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                    getView().hideLoading()
                    throwable.printStackTrace()
                    getView().handleApiError(throwable, { fetchVideo(video) })
                }
            }
    }

    fun playVideo() {
        if (remoteConfigManager.isOpenYoutubeNativePlayer) {
            getView().playVideoWithYoutubeNativeAppPlayer()
        } else {
            getView().playVideoWithYoutubeInAppPlayer()
        }
    }

    fun isSavedAsFavorite(videoItem: YoutubeItem): Boolean {
        return favoritesManager.findItemInFavorites(videoItem) != null
    }

    fun removeFavoriteItem(videoItem: YoutubeItem) {
        favoritesManager.removeChannelFavoriteVideo(videoItem)
    }

    fun putFavoriteItem(videoItem: YoutubeItem) {
        favoritesManager.putChannelFavoriteVideo(videoItem)
    }
}
