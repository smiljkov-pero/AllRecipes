package com.allrecipes.ui.home.views;

import com.allrecipes.model.Channel;
import com.allrecipes.model.FiltersAndSortSettings;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.ui.views.AbstractPresenterView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HomeScreenView extends AbstractPresenterView {
    void addYoutubeItemToAdapter(YoutubeItem item);
    void removeBottomListProgress();
    void clearAdapterItems();

    void initChannelsListOverlayAdapter(List<Channel> categories, int i);

    void setToolbarTitleText(@NotNull String value);

    void addSwapLaneChannelItemToAdapter(YoutubePlaylistWithVideos youtubePlaylistWithVideos);

    void setCurrentFilterSettings(FiltersAndSortSettings filtersAndSortSettings);
}
