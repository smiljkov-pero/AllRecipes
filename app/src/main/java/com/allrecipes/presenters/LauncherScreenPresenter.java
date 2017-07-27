package com.allrecipes.presenters;

import android.util.Log;

import com.allrecipes.di.managers.FirebaseDatabaseManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.managers.remoteconfig.RemoteConfigManager;
import com.allrecipes.model.Channel;
import com.allrecipes.model.DefaultChannel;
import com.allrecipes.ui.launcher.LauncherView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.GsonBuilder;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class LauncherScreenPresenter extends AbstractPresenter<LauncherView> {

    private static final String APP_LAST_CHANNEL_USED = "app.lastChannelUsed";

    private final RemoteConfigManager remoteConfigManager;
    private final FirebaseDatabaseManager firebaseDatabaseManager;
    private final LocalStorageManagerInterface localStorageManagerInterface;

    Subscription getCategoriesConfigFromFirebaseSubscription;

    public LauncherScreenPresenter(
        LauncherView view,
        RemoteConfigManager remoteConfigManager,
        FirebaseDatabaseManager firebaseDatabaseManager,
        LocalStorageManagerInterface localStorageManagerInterface
    ) {
        super(new WeakReference<>(view));
        this.remoteConfigManager = remoteConfigManager;
        this.firebaseDatabaseManager = firebaseDatabaseManager;
        this.localStorageManagerInterface = localStorageManagerInterface;
    }

    @Override
    public void unbindAll() {
        unsubscribe(getCategoriesConfigFromFirebaseSubscription);
    }

    public void onCreate() {
        if (remoteConfigManager.isRemoteConfigNotFetchYet()) {
            reloadFirebaseRemoteConfig(true);
        } else {
            fetchAppConfigFromFirebase(false);
            reloadFirebaseRemoteConfig(false);
            getView().startLoginActivity();
        }
    }

    private void reloadFirebaseRemoteConfig(final boolean proceedWithInit) {
        remoteConfigManager.reload(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    remoteConfigManager.activateFetched();

                    if (proceedWithInit) {
                        String remoteConfigString = remoteConfigManager.getDefaultChannel();
                        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, remoteConfigString);

                        fetchAppConfigFromFirebase(true);
                    }
                }
            }
        });
    }

    private void fetchAppConfigFromFirebase(final boolean initDefaultChannel) {
        getCategoriesConfigFromFirebaseSubscription = firebaseDatabaseManager.fetchChannels()
            .subscribe(
                new Action1<List<Channel>>() {
                    @Override
                    public void call(List<Channel> channels) {
                        if (isSubscribedAndViewAvailable(getCategoriesConfigFromFirebaseSubscription)) {
                            firebaseDatabaseManager.storeFirebaseConfig(channels);
                            if (initDefaultChannel) {
                                String remoteConfigString = remoteConfigManager.getDefaultChannel();
                                Channel defaultChannel = new GsonBuilder().create()
                                    .fromJson(remoteConfigString, DefaultChannel.class).defaultChannel;
                                for (Channel c : channels) {
                                    if (c.getChannelId().equals(defaultChannel.getChannelId())) {
                                        saveLastUsedChannel(c);
                                    }
                                }
                                getView().startLoginActivity();
                            }
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

    private void saveLastUsedChannel(Channel channel) {
        String categoryJson = new GsonBuilder().create().toJson(channel, Channel.class);
        localStorageManagerInterface.putString(APP_LAST_CHANNEL_USED, categoryJson);
    }
}
