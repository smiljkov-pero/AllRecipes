package com.allrecipes.ui.home.viewholders;


import com.allrecipes.R;
import com.mikepenz.fastadapter.utils.Function;

public class HomeScreenItemFactory implements Function<HomeScreenModelItemWrapper, HomeScreenItem> {

    private int imageHeight;

    public HomeScreenItemFactory(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public HomeScreenItem apply(HomeScreenModelItemWrapper wrapper) {
        switch (wrapper.getType()) {
            case R.id.home_screen_video_item:
                return new YoutubeItem(wrapper);
            case R.id.home_swimlane_channel_item:
                //return new SwipelaneChannelItem(wrapper);
                return null;
            default:
                return new HomeScreenItem(wrapper);
        }
    }
}
