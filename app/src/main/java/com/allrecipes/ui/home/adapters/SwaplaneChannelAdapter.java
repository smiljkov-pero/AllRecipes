package com.allrecipes.ui.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allrecipes.R;
import com.allrecipes.model.video.VideoItem;

import java.util.List;

public class SwaplaneChannelAdapter extends RecyclerView.Adapter<SwaplaneChannelAdapter.ViewHolder> {

    private List<VideoItem> items;

    public SwaplaneChannelAdapter(List<VideoItem> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_swaplane_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoItem item = items.get(position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View view) {
            super(view);
        }
    }
}
