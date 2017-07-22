package com.allrecipes.ui.home.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwapLaneChannelAdapter extends RecyclerView.Adapter<SwapLaneChannelAdapter.ViewHolder> {

    private List<VideoItem> items;
    private SwipeLaneItemClickListener listener;


    public SwapLaneChannelAdapter(List<VideoItem> items, SwipeLaneItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_swaplane_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         final VideoItem item = items.get(position);

        holder.name.setText(item.snippet.title);
        adjustYoutubeImage(holder, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSwapLaneItemClicked(v, item);
                }
            }
        });
    }

    private void adjustYoutubeImage(final ViewHolder holder, VideoItem item) {
        if (item.snippet != null && item.snippet.thumbnails != null)
            Picasso.with(holder.videoThumbnail.getContext())
                    .load(item.snippet.thumbnails.mediumThumbnail.url)
                    .fit()
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.videoThumbnail);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.videoThumbnail)
        ImageView videoThumbnail;

        @BindView(R.id.name)
        TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
