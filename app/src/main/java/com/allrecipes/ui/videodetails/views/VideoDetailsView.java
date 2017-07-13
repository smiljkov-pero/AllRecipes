package com.allrecipes.ui.videodetails.views;

import com.allrecipes.model.video.VideoItem;
import com.allrecipes.ui.views.AbstractPresenterView;

public interface VideoDetailsView extends AbstractPresenterView {
    void setVideoDetails(VideoItem item);
}
