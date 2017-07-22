package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.ui.home.adapters.SwapLaneChannelAdapter;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipeLaneChannelItem extends BaseHomeScreenItem {

    private YoutubePlaylistWithVideos item;

    public SwipeLaneChannelItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
        this.item = (YoutubePlaylistWithVideos) wrapper.getT();
    }

    public YoutubePlaylistWithVideos getItem() {
        return item;
    }

    @Override
    public int getType() {
        return R.id.home_swimlane_channel_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_swipelane_channel;
    }

    @Override
    public SwipeLineChannelViewHolder getViewHolder(View v) {
        return new SwipeLineChannelViewHolder(v);
    }

    @Override
    public void bindView(BaseViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        SwipeLineChannelViewHolder viewHolder = ((SwipeLineChannelViewHolder) holder);

        if(item.getChannel() != null) {
            viewHolder.name.setText(item.getChannel().getSnippet().title);
        }
        initSwapLane(viewHolder);
    }

    private void initSwapLane(SwipeLineChannelViewHolder viewHolder) {
        SwapLaneChannelAdapter adapter = new SwapLaneChannelAdapter(item.getVideosResponse().items);
        viewHolder.videosRecyclerView.setAdapter(adapter);
    }

    protected static class SwipeLineChannelViewHolder extends BaseViewHolder {

        @BindView(R.id.videosRecyclerView)
        RecyclerView videosRecyclerView;

        @BindView(R.id.name)
        TextView name;

        Context context;

        public SwipeLineChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
            LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            videosRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
