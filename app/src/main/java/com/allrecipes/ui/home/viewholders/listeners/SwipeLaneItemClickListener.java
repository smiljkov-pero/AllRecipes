package com.allrecipes.ui.home.viewholders.listeners;

import android.view.View;

import com.allrecipes.model.video.VideoItem;

public interface SwipeLaneItemClickListener {
    void onSwapLaneItemClicked(View view, VideoItem item);
}