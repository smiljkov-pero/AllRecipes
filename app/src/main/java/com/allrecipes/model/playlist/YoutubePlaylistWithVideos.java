package com.allrecipes.model.playlist;

import com.allrecipes.model.SearchChannelVideosResponse;
import com.allrecipes.model.video.YoutubeVideoResponse;

public class YoutubePlaylistWithVideos {
    private YoutubeChannelItem channel;
    private SearchChannelVideosResponse videosResponse;
    private int position;

    public YoutubePlaylistWithVideos(YoutubeChannelItem channel, SearchChannelVideosResponse videosResponse, int position) {
        this.channel = channel;
        this.videosResponse = videosResponse;
        this.position = position;
    }

    public YoutubeChannelItem getChannel() {
        return channel;
    }

    public void setChannel(YoutubeChannelItem channel) {
        this.channel = channel;
    }

    public SearchChannelVideosResponse getVideosResponse() {
        return videosResponse;
    }

    public void setVideosResponse(SearchChannelVideosResponse videosResponse) {
        this.videosResponse = videosResponse;
    }

    public int getPosition() {
        return position;
    }
}
