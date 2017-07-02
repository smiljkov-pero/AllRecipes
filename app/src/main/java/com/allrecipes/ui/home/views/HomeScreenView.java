package com.allrecipes.ui.home.views;

import com.allrecipes.model.YoutubeItem;
import com.allrecipes.ui.views.AbstractPresenterView;

public interface HomeScreenView extends AbstractPresenterView {
    void addYoutubeItemToAdapter(YoutubeItem item);
    void removeBottomListProgress();
}
