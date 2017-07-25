package com.allrecipes.ui.home.viewholders.listeners;

import android.view.View;

import com.allrecipes.model.video.VideoItem;
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem;

public interface SwipeLaneListener {
    void onSwapLaneItemClicked(View view, VideoItem item);
    void loadMoreOnSwipe(int position, SwipeLaneChannelItem item);
}