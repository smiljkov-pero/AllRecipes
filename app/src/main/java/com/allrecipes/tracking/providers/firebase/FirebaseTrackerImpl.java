package com.allrecipes.tracking.providers.firebase;

import android.app.Activity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

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
    public void setCurrentScreen(Activity activity, String str, String str2) {
        firebaseAnalytics.setCurrentScreen(activity, str, str2);
    }

    @Override
    public void logBreadCrumb(String breadCrumb) {
        Crashlytics.log(breadCrumb);
    }

    @Override
    public void logHandledException(Throwable throwable) {
        Crashlytics.logException(throwable);
    }
}
