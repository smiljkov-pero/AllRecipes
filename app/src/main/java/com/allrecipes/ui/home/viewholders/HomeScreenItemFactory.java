package com.allrecipes.ui.home.viewholders;


import com.allrecipes.R;
import com.allrecipes.ui.home.viewholders.items.HomeAdItem;
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem;
import com.allrecipes.ui.home.viewholders.items.YoutubeVideoItem;
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneItemClickListener;
import com.mikepenz.fastadapter.utils.Function;

public class HomeScreenItemFactory implements Function<HomeScreenModelItemWrapper, BaseHomeScreenItem> {

    private int imageHeight;

    public HomeScreenItemFactory(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public BaseHomeScreenItem apply(HomeScreenModelItemWrapper wrapper) {
        switch (wrapper.getType()) {
            case R.id.home_screen_video_item:
                return new YoutubeVideoItem(wrapper);
            case R.id.home_swimlane_channel_item:
                return new SwipeLaneChannelItem(wrapper, (SwipeLaneItemClickListener) wrapper.getListener());
            case R.id.item_home_ad:
                return new HomeAdItem(wrapper);
            default:
                return new BaseHomeScreenItem(wrapper);
        }
    }
}
