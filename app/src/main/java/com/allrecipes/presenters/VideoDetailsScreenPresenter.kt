package com.allrecipes.presenters

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
    private val remoteConfigManager: RemoteConfigManager
) : AbstractPresenter<VideoDetailsView>(view) {

    private var fetchVideoDisposable: Disposable? = null

    override fun unbindAll() {
        dispose(fetchVideoDisposable)
    }

    fun fetchVideo(videoId: String) {
        getView().showLoading()
        fetchVideoDisposable = googleYoutubeApiManager.fetchVideo(videoId)
            .subscribe({ response ->
                           if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                               getView().hideLoading()
                               getView().setVideoDetails(response.items[0])
                           }
                       }) { throwable ->
                if (isDisposedAndViewAvailable(fetchVideoDisposable)) {
                    getView().hideLoading()
                    throwable.printStackTrace()
                    getView().handleApiError(throwable, {fetchVideo(videoId)})
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
}
