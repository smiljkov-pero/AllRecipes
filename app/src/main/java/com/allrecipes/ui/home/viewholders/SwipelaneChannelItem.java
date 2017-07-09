package com.allrecipes.ui.home.viewholders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.ui.home.adapters.SwaplaneChannelAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipelaneChannelItem extends HomeChannelSwipelaneItem {

    private YoutubePlaylistWithVideos item;

    SwipelaneChannelItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
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
        return R.layout.item_youtube;
    }

    @Override
    public SwipelaneChannelItem.ViewHolder getViewHolder(View v) {
        return new SwipelaneChannelItem.ViewHolder(v);
    }

    @Override
    public void bindView(HomeChannelSwipelaneItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        SwipelaneChannelItem.ViewHolder viewHolder = ((SwipelaneChannelItem.ViewHolder) holder);

        viewHolder.name.setText(item.getChannel().getSnippet().channelTitle);
        initSwaplane(viewHolder);
    }

    private void initSwaplane(SwipelaneChannelItem.ViewHolder viewHolder) {
        SwaplaneChannelAdapter adapter = new SwaplaneChannelAdapter(item.getVideosResponse().items);
        viewHolder.videosRecyclerView.setAdapter(adapter);
    }

    protected static class ViewHolder extends HomeChannelSwipelaneItem.ViewHolder {

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
