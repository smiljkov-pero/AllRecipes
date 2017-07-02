package com.allrecipes.ui.home.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeItem extends HomeScreenItem {

    private com.allrecipes.model.YoutubeItem item;

    YoutubeItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
        this.item = (com.allrecipes.model.YoutubeItem) wrapper.getT();
    }

    public com.allrecipes.model.YoutubeItem getItem() {
        return item;
    }

    @Override
    public int getType() {
        return R.id.home_screen_video_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_youtube;
    }

    @Override
    public YoutubeItem.ViewHolder getViewHolder(View v) {
        return new YoutubeItem.ViewHolder(v);
    }

    @Override
    public void bindView(HomeScreenItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        YoutubeItem.ViewHolder viewHolder = ((YoutubeItem.ViewHolder) holder);

        adjustYoutubeImage(viewHolder, item);
        viewHolder.name.setText(item.snippet.title);
        viewHolder.description.setText(item.snippet.description);
    }

    private void adjustYoutubeImage(final ViewHolder holder, com.allrecipes.model.YoutubeItem item) {
        Picasso.with(holder.context)
            .load(item.snippet.thumbnails.highThumbnail.url)
            .fit()
            .centerCrop()
            //.placeholder(R.drawable.restaurant_placeholder)
            .config(Bitmap.Config.RGB_565)
            .into(holder.videoThumbnail);
    }

    protected static class ViewHolder extends HomeScreenItem.ViewHolder {

        @BindView(R.id.videoThumbnail)
        ImageView videoThumbnail;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;

        private final Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = itemView.getContext();
        }
    }
}
