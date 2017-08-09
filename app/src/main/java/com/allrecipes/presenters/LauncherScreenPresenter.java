package com.allrecipes.presenters;

import android.text.TextUtils;

import com.allrecipes.managers.FirebaseDatabaseManager;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.managers.remoteconfig.RemoteConfigManager;
import com.allrecipes.model.Channel;
import com.allrecipes.model.DefaultChannel;
import com.allrecipes.ui.launcher.LauncherView;
import com.allrecipes.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.GsonBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LauncherScreenPresenter extends AbstractPresenter<LauncherView> {

    private static final String APP_LAST_CHANNEL_USED = "app.lastChannelUsed";

    private final RemoteConfigManager remoteConfigManager;
    private final FirebaseDatabaseManager firebaseDatabaseManager;
    private final LocalStorageManagerInterface localStorageManager;

    private Disposable getCategoriesConfigFromFirebaseSubscription;

    public LauncherScreenPresenter(
        LauncherView view,
        RemoteConfigManager remoteConfigManager,
        FirebaseDatabaseManager firebaseDatabaseManager,
        LocalStorageManagerInterface localStorageManager
    ) {
        super(new WeakReference<>(view));
        this.remoteConfigManager = remoteConfigManager;
        this.firebaseDatabaseManager = firebaseDatabaseManager;
        this.localStorageManager = localStorageManager;
    }

    @Override
    public void unbindAll() {
        dispose(getCategoriesConfigFromFirebaseSubscription);
    }

    public void onCreate() {
        if (remoteConfigManager.isRemoteConfigNotFetchYet()) {
            // TODO Check for network available here
            reloadFirebaseRemoteConfig(true);
        } else {
            fetchAppConfigFromFirebase(false);
            reloadFirebaseRemoteConfig(false);
            checkIfUserWasLoggedInBefore();
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
                        localStorageManager.putString(APP_LAST_CHANNEL_USED, remoteConfigString);

                        fetchAppConfigFromFirebase(true);
                    }
                }
            }
        });
    }

    private void fetchAppConfigFromFirebase(final boolean initDefaultChannel) {
        getCategoriesConfigFromFirebaseSubscription = firebaseDatabaseManager.fetchChannels()
            .subscribe(
                new Consumer<DataSnapshot>() {
                   @Override
                   public void accept(@NonNull DataSnapshot dataSnapshot) throws Exception {
                       List<Channel> channels = new ArrayList<>();
                       for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                           Channel channel = postSnapshot.getValue(Channel.class);
                           channels.add(channel);
                       }
                       firebaseDatabaseManager.storeFirebaseConfig(channels);
                       if (isViewAvailable()) {
                           if (initDefaultChannel) {
                               String remoteConfigString = remoteConfigManager.getDefaultChannel();
                               Channel defaultChannel = new GsonBuilder().create()
                                   .fromJson(remoteConfigString, DefaultChannel.class).defaultChannel;
                               for (Channel c : channels) {
                                   if (c.getChannelId().equals(defaultChannel.getChannelId())) {
                                       saveLastUsedChannel(c);
                                   }
                               }
                               checkIfUserWasLoggedInBefore();
                           }
                       }
                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(@NonNull Throwable throwable) throws Exception {
                       throwable.printStackTrace();
                   }
               }
            );
    }

    private void saveLastUsedChannel(Channel channel) {
        String categoryJson = new GsonBuilder().create().toJson(channel, Channel.class);
        localStorageManager.putString(APP_LAST_CHANNEL_USED, categoryJson);
    }

    private void checkIfUserWasLoggedInBefore() {
        if (TextUtils.isEmpty(localStorageManager.getString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, ""))) {
            if (remoteConfigManager.showGoogleLogin()) {
                getView().startLoginActivity();
            } else {
                getView().startHomeActivity();
            }
        } else {
            getView().reloadLoggedInUser();
        }
    }

    public void onStart() {
        if (!TextUtils.isEmpty(localStorageManager.getString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, ""))) {
            getView().checkGoogleLogin();
        }
    }
}
