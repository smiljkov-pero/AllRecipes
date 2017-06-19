package com.allrecipes.util;

import android.content.SharedPreferences;

/**
 * Created by Vladimir on 11/14/2016.
 */

public class PreferenceManager implements AppPreferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(SharedPreferences preferences) {
        this.preferences = preferences;
        editor = preferences.edit();
    }

    public void setUserId(String userId) {
        editor.putString("app.userId", userId);
        editor.commit();
    }

    @Override
    public String getUserId() {
        return preferences.getString("app.userId", "noUserId");
    }

    @Override
    public void clearPreferences() {
        editor.clear();
        editor.commit();
    }
}
