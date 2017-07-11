package com.allrecipes.ui.home.views;

import com.allrecipes.model.Category;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.ui.views.AbstractPresenterView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HomeScreenView extends AbstractPresenterView {
    void addYoutubeItemToAdapter(YoutubeItem item);
    void removeBottomListProgress();
    void clearAdapterItems();

    void initAddressListOverlayAdapter(List<Category> categories, int i);

    void setToolbarTitleText(@NotNull String value);

    void addSwapLaneChannelItemToAdapter(YoutubePlaylistWithVideos youtubePlaylistWithVideos);
}
