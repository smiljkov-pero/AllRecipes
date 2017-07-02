package com.allrecipes.ui.videodetails.views;

import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.ui.views.AbstractPresenterView;

public interface VideoDetailsView extends AbstractPresenterView {
    void setVideoDetails(YoutubeSnipped item);
}
