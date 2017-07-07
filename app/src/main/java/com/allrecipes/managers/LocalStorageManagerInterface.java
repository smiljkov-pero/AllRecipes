package com.allrecipes.managers;

import java.util.Set;

public interface LocalStorageManagerInterface {
    String getString(String key);

    String getString(String key, String defaultValue);

    Boolean getBoolean(String key);

    Boolean getBoolean(String key, Boolean defaultValue);

    Set<String> getStringSet(String key);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    void putBoolean(String key, boolean value);

    void putInt(String key, int value);

    void putString(String key, String value);

    void putStringSet(String key, Set<String> value);

    void putLong(String key, long value);

    void remove(String key);
}
