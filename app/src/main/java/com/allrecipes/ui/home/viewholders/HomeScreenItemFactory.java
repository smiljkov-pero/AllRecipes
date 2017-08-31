package com.allrecipes.ui.home.viewholders;


import com.allrecipes.R;
import com.allrecipes.ui.home.listener.ScrollStateListener;
import com.allrecipes.ui.home.viewholders.items.HomeAdItem;
import com.allrecipes.ui.home.viewholders.items.SwipeAdItem;
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem;
import com.allrecipes.ui.home.viewholders.items.SwipeLaneVideoItem;
import com.allrecipes.ui.home.viewholders.items.YoutubeVideoItem;
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneListener;
import com.mikepenz.fastadapter.utils.Function;

import de.foodora.android.networkutils.NetworkQuality;

public class HomeScreenItemFactory implements Function<HomeScreenModelItemWrapper, BaseHomeScreenItem> {

    private final NetworkQuality networkQuality;

    public HomeScreenItemFactory(NetworkQuality networkQuality) {
        this.networkQuality = networkQuality;
    }

    @Override
    public BaseHomeScreenItem apply(HomeScreenModelItemWrapper wrapper) {
        switch (wrapper.getType()) {
            case R.id.home_screen_video_item:
                return new YoutubeVideoItem(wrapper, networkQuality);
            case R.id.home_swimlane_channel_item:
                return new SwipeLaneChannelItem(wrapper, (SwipeLaneListener) wrapper.getListener(), networkQuality);
            case R.id.home_ad_item:
                return new HomeAdItem(wrapper);
            case R.id.swipelane_video_item:
                return new SwipeLaneVideoItem(wrapper, networkQuality);
            case R.id.swipelane_ad_item:
                return new SwipeAdItem(wrapper);
            default:
                return new BaseHomeScreenItem(wrapper);
        }
    }
}
