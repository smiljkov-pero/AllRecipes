package com.allrecipes.ui.home.views;

import com.allrecipes.model.Youtube;
import com.allrecipes.ui.views.AbstractPresenterView;

public interface HomeScreenView extends AbstractPresenterView {
    void addYoutubeItemToAdapter(Youtube item);
}
