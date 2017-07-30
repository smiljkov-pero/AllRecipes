package com.allrecipes.managers.remoteconfig;

import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;

import de.foodora.android.managers.remoteconfig.ForceUpdate;

public interface RemoteConfigManager {
    boolean isOpenYoutubeNativePlayer();

    boolean showGoogleLogin();

    boolean canSkipLogin();

    String getDefaultChannel();

    String getDefaultFilterSort();

    long getVideoListItemsPerPage();

    List<String> getFilterCategories();

    void reload(OnCompleteListener listener);

    void activateFetched();

    boolean isRemoteConfigNotFetchYet();

    ForceUpdate getForceUpdate();

    int getNumberOfAdsPerItem();
}
