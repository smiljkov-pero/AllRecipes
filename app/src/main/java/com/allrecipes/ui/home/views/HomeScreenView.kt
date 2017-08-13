package com.allrecipes.ui.home.views

import com.allrecipes.model.Channel
import com.allrecipes.model.FiltersAndSortSettings
import com.allrecipes.model.YoutubeItem
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos
import com.allrecipes.ui.views.AbstractPresenterView

interface HomeScreenView : AbstractPresenterView {
    fun addYoutubeItemToAdapter(item: YoutubeItem, position: Int)

    fun removeBottomListProgress()

    fun clearAdapterItems()

    fun initChannelsListOverlayAdapter(categories: List<Channel>, i: String)

    fun setToolbarTitleText(value: String)

    fun addSwapLaneChannelItemToAdapter(youtubePlaylistWithVideos: YoutubePlaylistWithVideos, position: Int)

    fun setCurrentFilterSettings(filtersAndSortSettings: FiltersAndSortSettings)
    fun showFiltersTooltip()
}
