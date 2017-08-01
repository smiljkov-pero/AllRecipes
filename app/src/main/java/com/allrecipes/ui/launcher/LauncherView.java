package com.allrecipes.ui.launcher;

import com.allrecipes.ui.views.AbstractPresenterView;

public interface LauncherView extends AbstractPresenterView {

    void startLoginActivity();

    void reloadLoggedInUser();

    void checkGoogleLogin();

    void startHomeActivity();
}
