package com.allrecipes.ui.launcher;

import android.accounts.Account;

import com.allrecipes.ui.views.AbstractPresenterView;

public interface LauncherView extends AbstractPresenterView {

    void startLoginActivity(String token);

    void getGoogleAuthToken(Account account);
}
