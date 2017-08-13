package com.allrecipes.tracking.providers.firebase;

import android.accounts.Account;

public class UserPropertiesManager {

    private static final String PROPERTY_EMAIL = "email";
    private static final String PROPERTY_COUNTRY = "country_code";
    private final FirebaseTracker firebaseAnalytics;

    public UserPropertiesManager(FirebaseTracker firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public void setBasicUserProperties(Account googleAccount) {
        if (googleAccount != null) {
            firebaseAnalytics.setUserProperty(PROPERTY_EMAIL, googleAccount.name);
        }
    }

    public void setUserCountry(String countryCode) {
        firebaseAnalytics.setUserProperty(PROPERTY_COUNTRY, countryCode);
    }

    public void clearUserProperties() {
        firebaseAnalytics.setUserProperty(PROPERTY_EMAIL, "");
    }
}
