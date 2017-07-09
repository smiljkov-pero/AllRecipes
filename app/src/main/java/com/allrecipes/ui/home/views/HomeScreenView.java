package com.allrecipes.ui.home.views;

import com.allrecipes.model.Category;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.ui.views.AbstractPresenterView;

import java.util.List;

public interface HomeScreenView extends AbstractPresenterView {
    void addYoutubeItemToAdapter(YoutubeItem item);
    void removeBottomListProgress();
    void clearAdapterItems();

    void initAddressListOverlayAdapter(List<Category> categories, int i);
}
