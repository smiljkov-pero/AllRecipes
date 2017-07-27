package com.allrecipes.ui.launcher;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.allrecipes.R;
import com.allrecipes.presenters.LauncherScreenPresenter;
import com.allrecipes.ui.BaseActivity;
import com.allrecipes.ui.LoginActivity;
import com.allrecipes.ui.home.activity.HomeActivity;
import com.allrecipes.util.Constants;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.Collections;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
