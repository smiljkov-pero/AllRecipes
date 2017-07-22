package com.allrecipes.managers.remoteconfig;

import com.google.android.gms.tasks.OnCompleteListener;

import de.foodora.android.managers.remoteconfig.ForceUpdate;

public interface RemoteConfigManager {
    boolean isOpenYoutubeNativePlayer();

    String getDefaultChannel();

    long getVideoListItemsPerPage();

    void reload(OnCompleteListener listener);

    void activateFetched();

    boolean isRemoteConfigNotFetchYet();

    ForceUpdate getForceUpdate();
}
