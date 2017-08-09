package com.allrecipes.tracking.providers.firebase;

import android.app.Activity;
import android.os.Bundle;

public interface FirebaseTracker {
    void logEvent(String eventName, Bundle bundle);

    void setUserProperty(String property, String value);

    void setUserId(String userId);

    void setCurrentScreen(Activity activity, String str, String str2);

    void logBreadCrumb(String breadCrumb);

    void logHandledException(Throwable throwable);
}
