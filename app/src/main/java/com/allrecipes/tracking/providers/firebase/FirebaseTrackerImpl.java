package com.allrecipes.tracking.providers.firebase;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

public class FirebaseTrackerImpl implements FirebaseTracker {

    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseTrackerImpl(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void logEvent(String eventName, Bundle bundle) {
        firebaseAnalytics.logEvent(eventName, bundle);
    }

    @Override
    public void setUserProperty(String property, String value) {
        firebaseAnalytics.setUserProperty(property, value);
    }

    @Override
    public void setUserId(String userId) {
        firebaseAnalytics.setUserId(userId);
    }

    @Override
    public void setCurrentScreen(Activity activity, String str, String str2) {
        firebaseAnalytics.setCurrentScreen(activity, str, str2);
    }

    @Override
    public void logBreadCrumb(String breadCrumb) {
        FirebaseCrash.log(breadCrumb);
    }

    @Override
    public void logHandledException(Throwable throwable) {
        FirebaseCrash.report(throwable);
    }
}
