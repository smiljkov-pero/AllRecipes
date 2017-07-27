package com.allrecipes.ui.launcher;

import android.accounts.Account;
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

        /*AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                getGoogleAuthToken(account);
                return;
            }
        }*/

        presenter.onCreate(null);
    }

    @Override
    public void startLoginActivity(String token) {
        if (TextUtils.isEmpty(token)) {
            startActivity(LoginActivity.newIntent(this));
        } else {
            startActivity(HomeActivity.Companion.newIntent(this, token));
        }
    }

    @Override
    public void getGoogleAuthToken(final Account account) {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    LauncherActivity.this,
                    Collections.singleton(Constants.YOUTUBE_SCOPE)
                );
                credential.setSelectedAccount(account);

                return credential.getToken();
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull String token) throws Exception {
                    presenter.onCreate(token);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                    startLoginActivity(null);
                }
            });

    }
}
