package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.video.VideoItem;
import com.allrecipes.ui.home.adapters.SwipeLaneChannelAdapter;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipeLaneVideoItem extends BaseHomeScreenItem {

    private VideoItem item;

    public SwipeLaneVideoItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
        this.item = (VideoItem) wrapper.getT();
    }

    public VideoItem getItem() {
        return item;
    }

    @Override
    public int getType() {
        return R.id.swipelane_video_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_swaplane_video;
    }

    @Override
    public SwipeLaneVideoItem.ViewHolder getViewHolder(View v) {
        return new SwipeLaneVideoItem.ViewHolder(v);
    }

    @Override
    public void bindView(BaseViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        SwipeLaneVideoItem.ViewHolder viewHolder = ((SwipeLaneVideoItem.ViewHolder) holder);

        viewHolder.name.setText(item.snippet.title);
        adjustYoutubeImage(viewHolder, item);

    }

    private void adjustYoutubeImage(final SwipeLaneVideoItem.ViewHolder holder, VideoItem item) {
        if (item.snippet != null && item.snippet.thumbnails != null)
            Picasso.with(holder.videoThumbnail.getContext())
                .load(item.snippet.thumbnails.mediumThumbnail.url)
                .fit()
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .into(holder.videoThumbnail);
    }
    protected static class ViewHolder extends BaseViewHolder {

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
