package com.allrecipes.ui.videodetails.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.custom.LinkTransformationMethod;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.presenters.VideoDetailsScreenPresenter;
import com.allrecipes.ui.BaseActivity;
import com.allrecipes.ui.YoutubePlayerActivity;
import com.allrecipes.ui.videodetails.views.VideoDetailsView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends BaseActivity implements VideoDetailsView {

    private static final String KEY_VIDEO = "KEY_VIDEO";
    final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static final String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
    private static final String two = "0$1";

    @BindView(R.id.viewGrayOverlay)
    View viewGrayOverlay;
    @BindView(R.id.video_thumbnail)
    ImageView videoThumbnail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.recipe_title)
    TextView recipeTitle;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.number_likes)
    TextView numberOfLikes;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.number_likes_icon)
    ImageView numberOfLikesIcon;
    @BindView(R.id.number_views)
    TextView numberViews;
    @BindView(R.id.number_views_label)
    TextView numberViewsLabel;
    @BindView(R.id.publishedDate)
    TextView publishedDate;
    @BindView(R.id.description_divider)
    View descriptionDivider;
    @BindView(R.id.favoriteIcon)
    ImageView favoriteIcon;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.adViewBelowImage)
    AdView adViewBelowImage;
    @BindView(R.id.adViewBelowTextDescription)
    AdView adViewBelowTextDescription;

    @Inject
    VideoDetailsScreenPresenter presenter;

    CustomTabsClient mCustomTabsClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    CustomTabsIntent mCustomTabsIntent;

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

        presenter.fetchVideo(video);
        initAds();

        Picasso.with(this)
            .load(video.snippet.thumbnails.highThumbnail.url)
            .placeholder(R.drawable.ic_item_placeholder)
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

        setTitleToolbar(video.snippet.title);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                titleText.setAlpha(
                    Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange())
                );
            }
        });

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mCustomTabsClient= customTabsClient;
                mCustomTabsClient.warmup(0L);
                mCustomTabsSession = mCustomTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient= null;
            }
        };

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        mCustomTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setShowTitle(true)
                .build();
    }

    private void initAds() {
        AdRequest adViewBelowImageRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("892B020FF18C6AB6C3F019DF8029AACE")
            .build();
        adViewBelowImage.loadAd(adViewBelowImageRequest);

        AdRequest adBlowTextDescriptionRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("892B020FF18C6AB6C3F019DF8029AACE")
            .build();
        adViewBelowTextDescription.loadAd(adBlowTextDescriptionRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            presenter.unbindAll();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void setTitleToolbar(String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
        titleText.setText(title);
    }

    @OnClick(R.id.video_thumbnail)
    void onThumbnailClick() {
        presenter.playVideo();
    }

    @OnClick(R.id.title_text)
    void onToolbarTitleClick() {
        presenter.playVideo();
    }

    @OnClick(R.id.recipe_title)
    void onRecipeTitleClick() {
        presenter.playVideo();
    }

    @Override
    public void playVideoWithYoutubeInAppPlayer() {
        startActivity(YoutubePlayerActivity.newIntent(this, video.id.videoId));
    }

    @Override
    public void playVideoWithYoutubeNativeAppPlayer() {
        startActivity(new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=" + video.id.videoId
        )));
    }

    @Override
    public void setVideoDetails(VideoItem item, boolean isFavoriteSaved) {
        description.setText(Html.fromHtml(item.snippet.description.replace("\n", "<br>")));
        description.setTransformationMethod(new LinkTransformationMethod(mCustomTabsIntent, this));
        description.setMovementMethod(LinkMovementMethod.getInstance());
        recipeTitle.setText(item.snippet.title);

        int likeCount = Integer.parseInt(item.statistics.likeCount);
        boolean isLikeCountBiggerThenThousand = likeCount >= 1000;
        if (isLikeCountBiggerThenThousand) {
            likeCount = likeCount / 1000;
        }
        numberOfLikes.setText(
            isLikeCountBiggerThenThousand
                ? likeCount + "K"
                : item.statistics.likeCount
        );
        numberOfLikesIcon.setVisibility(View.VISIBLE);

        int viewsCount = Integer.parseInt(item.statistics.viewCount);
        boolean isViewCountBiggerThenThousand = viewsCount >= 1000;
        if (isViewCountBiggerThenThousand) {
            viewsCount = viewsCount / 1000;
        }
        numberViews.setText(
            isViewCountBiggerThenThousand
                ? viewsCount + "K"
                : item.statistics.viewCount
        );
        numberViewsLabel.setVisibility(View.VISIBLE);

        publishedDate.setText(DateUtils.getRelativeTimeSpanString(
            item.snippet.publishedAt.getTime(),
            Calendar.getInstance().getTime().getTime(),
            0
        ));
        descriptionDivider.setVisibility(View.VISIBLE);
        favoriteIcon.setVisibility(View.VISIBLE);
        if (isFavoriteSaved) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
            favoriteIcon.setTag("selected");
        }
        setDurationValue(item.contentDetails.duration);
    }

    private void setDurationValue(String durationValue) {
        HashMap<String, String> durationRegexMap = new HashMap<>();
        durationRegexMap.put("PT(\\d\\d)S", "00:$1");
        durationRegexMap.put("PT(\\d\\d)M", "$1:00");
        durationRegexMap.put("PT(\\d\\d)H", "$1:00:00");
        durationRegexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
        durationRegexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
        durationRegexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
        durationRegexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");

        String d = durationValue.replaceAll(regex2two, two);
        String regex = getRegex(d, durationRegexMap);
        if (regex != null) {
            String newDurationValue = d.replaceAll(regex, durationRegexMap.get(regex));
            duration.setText(newDurationValue);
            duration.setVisibility(View.VISIBLE);
        }
    }

    private String getRegex(String date, HashMap<String, String> durationRegexMap) {
        for (String r : durationRegexMap.keySet())
            if (Pattern.matches(r, date))
                return r;
        return null;
    }

    @OnClick(R.id.favoriteIcon)
    public void onFavoriteIconClick() {
        if (favoriteIcon.getTag().equals("selected")) {
            presenter.removeFavoriteItem(video);
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            favoriteIcon.setTag("notSelected");
        } else {
            presenter.putFavoriteItem(video);
            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
            favoriteIcon.setTag("selected");
        }
    }
}
