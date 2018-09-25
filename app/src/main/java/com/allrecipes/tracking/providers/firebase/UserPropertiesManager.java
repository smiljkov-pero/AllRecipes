package com.allrecipes.tracking.providers.firebase;

import android.accounts.Account;

public class UserPropertiesManager {

    private final FirebaseTracker firebaseAnalytics;

    public UserPropertiesManager(FirebaseTracker firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }
}
