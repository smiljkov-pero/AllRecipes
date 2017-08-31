package com.allrecipes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.allrecipes.App;
import com.allrecipes.BuildConfig;
import com.allrecipes.R;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.tracking.providers.firebase.FirebaseTracker;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String KEY_VIDEO_URL = "KEY_VIDEO_URL";

    //http://youtu.be/<VIDEO_ID>
    public String videoUrl;

    @Inject
    FirebaseTracker firebaseTracker;

    public static Intent newIntent(Context context, String videoUrl) {
        Intent intent = new Intent(context, YoutubePlayerActivity.class);
        intent.putExtra(KEY_VIDEO_URL, videoUrl);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        ((App) getApplicationContext()).getAppComponent().inject(this);

        setStatusBarColor(android.R.color.transparent);
        setTransparentStatusBar(getWindow().getDecorView());

        if (savedInstanceState == null) {
            videoUrl = getIntent().getStringExtra(KEY_VIDEO_URL);
        } else {
            videoUrl = savedInstanceState.getString(KEY_VIDEO_URL);
        }

        /** Initializing YouTube player view **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_VIDEO_URL, videoUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        bundle.putBoolean("init", false);
        bundle.putString("video_id", videoUrl);
        firebaseTracker.logEvent("yp_failure_init", bundle);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            player.cueVideo(videoUrl);
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            Bundle bundle = new Bundle();
            bundle.putString("video_id", videoUrl);
            bundle.putString("error", arg0.toString());
            firebaseTracker.logEvent("yp_on_error", bundle);
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };

    /**
     * @param decorView the view obtained form {@code getWindow().getDecorView()}
     */
    public static void setTransparentStatusBar(View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    protected void setStatusBarColor(int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(colorRes));
        }
    }
}
