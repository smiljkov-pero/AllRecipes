package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
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
    public BaseHomeScreenItem.ViewHolder getViewHolder(View v) {
        return new BaseHomeScreenItem.ViewHolder(v);
    }

    @Override
    public void bindView(BaseHomeScreenItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        SwipeLaneChannelItem.ViewHolder viewHolder = ((SwipeLaneChannelItem.ViewHolder) holder);

        viewHolder.name.setText(item.getChannel().getSnippet().channelTitle);
        initSwapLane(viewHolder);
    }

    private void initSwapLane(SwipeLaneChannelItem.ViewHolder viewHolder) {
        SwapLaneChannelAdapter adapter = new SwapLaneChannelAdapter(item.getVideosResponse().items);
        viewHolder.videosRecyclerView.setAdapter(adapter);
    }

    protected static class ViewHolder extends BaseHomeScreenItem.ViewHolder {

        @BindView(R.id.videosRecyclerView)
        RecyclerView videosRecyclerView;

        @BindView(R.id.name)
        TextView name;

        Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
            LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            videosRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
