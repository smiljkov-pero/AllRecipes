package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos;
import com.allrecipes.model.video.YoutubeVideoResponse;
import com.allrecipes.ui.home.adapters.SwipeLaneChannelAdapter;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneListener;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipeLaneChannelItem extends BaseHomeScreenItem {

    private YoutubePlaylistWithVideos item;
    private SwipeLaneListener listener;
    private SwipeLaneChannelAdapter adapter;

    public SwipeLaneChannelItem(HomeScreenModelItemWrapper wrapper, SwipeLaneListener listener) {
        super(wrapper);
        this.item = (YoutubePlaylistWithVideos) wrapper.getT();
        this.listener = listener;
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

    public void loadMore(YoutubeVideoResponse youtubeVideoResponse){
        item.getVideosResponse().items.addAll(youtubeVideoResponse.items);
        item.getVideosResponse().nextPageToken = youtubeVideoResponse.nextPageToken;
        //item.getVideosResponse().prevPageToken = youtubeVideoResponse.prevPageToken;
        adapter.notifyDataSetChanged();
    }

    private void initSwapLane(SwipeLineChannelViewHolder viewHolder) {
        adapter = new SwipeLaneChannelAdapter(item.getVideosResponse().items, listener);
        viewHolder.videosRecyclerView.setAdapter(adapter);

        viewHolder.videosRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                if(listener != null) {
                    listener.loadMoreOnSwipe(0, SwipeLaneChannelItem.this);
                }
            }
        });
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
