package com.allrecipes.ui.videodetails.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.presenters.VideoDetailsScreenPresenter;
import com.allrecipes.ui.BaseActivity;
import com.allrecipes.ui.YoutubePlayerActivity;
import com.allrecipes.ui.videodetails.views.VideoDetailsView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends BaseActivity implements VideoDetailsView {

    private static final String KEY_VIDEO = "KEY_VIDEO";

    @BindView(R.id.viewGrayOverlay)
    View viewGrayOverlay;
    @BindView(R.id.video_thumbnail)
    ImageView videoThumbnail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.description)
    TextView description;

    @Inject
    VideoDetailsScreenPresenter presenter;

    private YoutubeItem video;
    private boolean isAnimationStarted = false;

    public static Intent newIntent(Context context, YoutubeItem video) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(KEY_VIDEO, video);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getApp().createVideoDetailsScreenComponent(this).inject(this);
        ButterKnife.bind(this);

        setStatusBarColor(android.R.color.transparent);
        setTransparentStatusBar(getWindow().getDecorView());
        supportPostponeEnterTransition();

        if (isAtLeastLollipop()) {
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    isAnimationStarted = true;
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    AlphaAnimation tagAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                    tagAlphaAnimation.setInterpolator(new DecelerateInterpolator());
                    tagAlphaAnimation.setDuration(300L);
                    tagAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (viewGrayOverlay != null) {
                                viewGrayOverlay.setLayerType(View.LAYER_TYPE_NONE, null);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    viewGrayOverlay.setVisibility(View.VISIBLE);
                    viewGrayOverlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    viewGrayOverlay.startAnimation(tagAlphaAnimation);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        } else {
            viewGrayOverlay.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState == null) {
            video = getIntent().getParcelableExtra(KEY_VIDEO);
        } else {
            video = savedInstanceState.getParcelable(KEY_VIDEO);
        }

        presenter.fetchVideo(video.id.videoId);

        Picasso.with(this)
                .load(video.snippet.thumbnails.highThumbnail.url)
                //.placeholder(R.drawable.restaurant_placeholder)
                .noFade()
                .into(videoThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
        setSupportActionBar(toolbar);
        setTitleToolbar(video.snippet.title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_VIDEO, video);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isAnimationStarted) {
            viewGrayOverlay.setVisibility(View.VISIBLE);
        }
    }

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
        if (isAtLeastLollipop()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(colorRes));
        }
    }

    private void setTitleToolbar(String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    @OnClick(R.id.video_thumbnail)
    void onThumbnailClick() {
        startActivity(YoutubePlayerActivity.newIntent(this, video.id.videoId));
    }

    @Override
    public void setVideoDetails(YoutubeSnipped item) {
        description.setText(Html.fromHtml(item.description.replace("\n", "<br>")));
    }
}
