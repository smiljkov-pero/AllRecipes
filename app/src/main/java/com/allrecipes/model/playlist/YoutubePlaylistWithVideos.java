package com.allrecipes.model.playlist;

import com.allrecipes.model.video.YoutubeVideoResponse;

public class YoutubePlaylistWithVideos {
    private YoutubeChannelItem channel;
    private YoutubeVideoResponse videosResponse;
    private int position;

    public YoutubePlaylistWithVideos(YoutubeChannelItem channel, YoutubeVideoResponse videosResponse, int position) {
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

    public YoutubeVideoResponse getVideosResponse() {
        return videosResponse;
    }

    public void setVideosResponse(YoutubeVideoResponse videosResponse) {
        this.videosResponse = videosResponse;
    }

    public int getPosition() {
        return position;
    }
}
