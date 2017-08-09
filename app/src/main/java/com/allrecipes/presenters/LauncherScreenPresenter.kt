package com.allrecipes.presenters

import android.text.TextUtils

import com.allrecipes.managers.FirebaseDatabaseManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.model.Channel
import com.allrecipes.model.DefaultChannel
import com.allrecipes.network.OfflineException
import com.allrecipes.ui.launcher.LauncherView
import com.allrecipes.util.Constants
import com.allrecipes.util.NetworkUtils
import com.google.gson.GsonBuilder

import java.lang.ref.WeakReference

import io.reactivex.disposables.Disposable

class LauncherScreenPresenter(
    view: WeakReference<LauncherView>,
    private val remoteConfigManager: RemoteConfigManager,
    private val firebaseDatabaseManager: FirebaseDatabaseManager,
    private val localStorageManager: LocalStorageManagerInterface,
    private val networkUtils: NetworkUtils
) : AbstractPresenter<LauncherView>(view) {

    companion object {
        private val APP_LAST_CHANNEL_USED = "app.lastChannelUsed"
    }

    private var getCategoriesConfigFromFirebaseSubscription: Disposable? = null

    override fun unbindAll() {
        dispose(getCategoriesConfigFromFirebaseSubscription)
    }

    fun onResume(reloadConfig: Boolean) {
        if (remoteConfigManager.isRemoteConfigNotFetchYet || reloadConfig) {
            if (networkUtils.isNetworkAvailable) {
                reloadFirebaseRemoteConfig(true)
            } else {
                getView().handleApiError(OfflineException(), { onResume(false) })
            }
        } else {
            if (!firebaseDatabaseManager.restoreFirebaseConfig().isEmpty()
                && remoteConfigManager.defaultChannel != null
            ) {
                fetchAppConfigFromFirebase(false)
                reloadFirebaseRemoteConfig(false)
                checkIfUserWasLoggedInBefore()
            } else {
                getView().handleApiError(OfflineException(), { onResume(true) })
            }
        }
    }

    private fun reloadFirebaseRemoteConfig(proceedWithInit: Boolean) {
        remoteConfigManager.reload { task ->
            if (task.isSuccessful) {
                remoteConfigManager.activateFetched()
                if (proceedWithInit) {
                    val remoteConfigString = remoteConfigManager.defaultChannel
                    localStorageManager.putString(APP_LAST_CHANNEL_USED, remoteConfigString)

                    fetchAppConfigFromFirebase(true)
                }
            }
        }
    }

    private fun fetchAppConfigFromFirebase(initDefaultChannel: Boolean) {
        getCategoriesConfigFromFirebaseSubscription = firebaseDatabaseManager.fetchChannels()
            .subscribe(
                { dataSnapshot ->
                    val channels = dataSnapshot.children
                        .map { it.getValue(Channel::class.java) }
                        .map { it!! }
                    firebaseDatabaseManager.storeFirebaseConfig(channels)
                    if (isViewAvailable) {
                        if (initDefaultChannel) {
                            val remoteConfigString = remoteConfigManager.defaultChannel
                            val defaultChannel = GsonBuilder().create()
                                .fromJson(remoteConfigString, DefaultChannel::class.java).defaultChannel
                            channels
                                .filter { it.channelId == defaultChannel.channelId }
                                .forEach { saveLastUsedChannel(it) }
                            checkIfUserWasLoggedInBefore()
                        }
                    }
                }
            ) { throwable -> throwable.printStackTrace() }
    }

    private fun saveLastUsedChannel(channel: Channel) {
        val categoryJson = GsonBuilder().create().toJson(channel, Channel::class.java)
        localStorageManager.putString(APP_LAST_CHANNEL_USED, categoryJson)
    }

    private fun checkIfUserWasLoggedInBefore() {
        if (TextUtils.isEmpty(localStorageManager.getString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, ""))) {
            if (remoteConfigManager.showGoogleLogin()) {
                getView().startLoginActivity()
            } else {
                getView().startHomeActivity()
            }
        } else {
            getView().reloadLoggedInUser()
        }
    }

    fun onStart() {
        if (!TextUtils.isEmpty(localStorageManager.getString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, ""))) {
            getView().checkGoogleLogin()
        }
    }
}
