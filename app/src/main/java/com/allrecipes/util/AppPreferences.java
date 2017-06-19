package com.allrecipes.util;

/**
 * Created by Vladimir on 11/14/2016.
 */
public interface AppPreferences {

    void setUserId(String userId);

    String getUserId();

    void clearPreferences();
}
