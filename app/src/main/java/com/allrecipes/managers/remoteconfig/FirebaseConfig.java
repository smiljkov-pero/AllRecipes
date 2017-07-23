package com.allrecipes.managers.remoteconfig;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.foodora.android.managers.remoteconfig.ForceUpdate;

public class FirebaseConfig implements RemoteConfigManager {

    private static final String OPEN_YOUTUBE_NATIVE_PLAYER = "open_youtube_native_player";
    private static final String DEFAULT_CHANNEL = "default_channel";
    private static final String VIDEO_LIST_ITEMS_PER_PAGE = "video_list_items_per_page";
    private static final String FILTER_CATEGORIES = "filter_categories";
    private static final String DEFAULT_FILTER_SORT = "default_filter_sort";
    private static final String APP_FORCE_UPDATE_VERSION = "app_force_update_version";

    private final FirebaseRemoteConfig remoteConfig;

    public FirebaseConfig(FirebaseRemoteConfig remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    @Override
    public boolean isOpenYoutubeNativePlayer() {
        return remoteConfig.getBoolean(OPEN_YOUTUBE_NATIVE_PLAYER);
    }

    @Override
    public String getDefaultChannel() {
        return remoteConfig.getString(DEFAULT_CHANNEL);
    }

    @Override
    public String getDefaultFilterSort() {
        return remoteConfig.getString(DEFAULT_FILTER_SORT);
    }

    @Override
    public long getVideoListItemsPerPage() {
        return remoteConfig.getLong(VIDEO_LIST_ITEMS_PER_PAGE);
    }

    @Override
    public List<String> getFilterCategories() {
        Type listType = new TypeToken<List<String>>(){}.getType();
        String filterCategories = remoteConfig.getString(FILTER_CATEGORIES);

        return new Gson().fromJson(filterCategories, listType);
    }

    @Override
    public void reload(OnCompleteListener listener) {
        remoteConfig.fetch(10L).addOnCompleteListener(listener);
    }

    @Override
    public void activateFetched() {
        remoteConfig.activateFetched();
    }

    @Override
    public boolean isRemoteConfigNotFetchYet() {
        return remoteConfig.getInfo().getLastFetchStatus()
            == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET;
    }

    @Override
    public ForceUpdate getForceUpdate() {
        String config = remoteConfig.getString(APP_FORCE_UPDATE_VERSION);
        try {
            return config != null ? new Gson().fromJson(config, ForceUpdate.class) : new ForceUpdate();
        } catch (Exception e) {
            return new ForceUpdate();
        }
    }
}
