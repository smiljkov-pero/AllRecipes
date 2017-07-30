package com.allrecipes.ui.launcher;

import android.os.Bundle;

import com.allrecipes.R;
import com.allrecipes.presenters.LauncherScreenPresenter;
import com.allrecipes.ui.BaseActivity;
import com.allrecipes.ui.LoginActivity;

import javax.inject.Inject;

public class LauncherActivity extends BaseActivity implements LauncherView {

    @Inject
    LauncherScreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getApp().createLauncherScreenComponent(this).inject(this);

        presenter.onCreate();
    }

    @Override
    public void startLoginActivity() {
        startActivity(LoginActivity.newIntent(this));
    }
}
