package com.allrecipes.ui;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.managers.remoteconfig.RemoteConfigManager;
import com.allrecipes.ui.home.activity.HomeActivity;
import com.allrecipes.util.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.skip_login)
    TextView skipLogin;

    private static final String TAG = "LoginActivity";
    private static final String KEY_ACCOUNT = "key_account";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_RECOVERABLE = 9002;
    private static final int RC_HOME_SCREEN = 9003;

    private GoogleApiClient googleApiClient;
    private Account account;
    private ProgressDialog progressDialog;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);

        return intent;
    }

    @Inject
    RemoteConfigManager remoteConfigManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getApp().getAppComponent().inject(this);

        if (savedInstanceState != null) {
            account = savedInstanceState.getParcelable(KEY_ACCOUNT);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(new Scope(Constants.YOUTUBE_SCOPE))
            .requestEmail()
            .build();

        googleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        skipLogin.setVisibility(remoteConfigManager.canSkipLogin() ? View.VISIBLE : View.GONE);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            if (remoteConfigManager.showGoogleLogin()) {
                showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        hideProgressDialog();
                        handleSignInResult(googleSignInResult);
                    }
                });
            } else {
                startHomeActivity();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ACCOUNT, account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == RC_RECOVERABLE) {
            if (resultCode == RESULT_OK) {
                startHomeActivityWithGoogleAccount();
            } else {
                //Toast.makeText(this, R.string.msg_contacts_failed, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == RC_HOME_SCREEN) {
            finish();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    //updateUI(false);
                }
            });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            this.account = account.getAccount();

            startHomeActivityWithGoogleAccount();
        } else {
            account = null;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
    }

    @OnClick(R.id.sign_in_button)
    public void onClickSignInButton() {
        signIn();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    void startHomeActivity() {
        Intent intent = HomeActivity.Companion.newIntent(this);
        startActivity(intent);
    }

    void startHomeActivityWithGoogleAccount() {
        Intent intent = HomeActivity.Companion.newIntent(this, account);
        startActivityForResult(intent, RC_HOME_SCREEN);
    }

    @OnClick(R.id.skip_login)
    public void onSkipLoginClick() {
        Intent intent = HomeActivity.Companion.newIntent(this);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
