package com.allrecipes.ui.home.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import com.allrecipes.R;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeItem extends HomeScreenItem {

    private com.allrecipes.model.YoutubeItem item;

    YoutubeItem(
        HomeScreenModelItemWrapper wrapper
    ) {
        super(wrapper);
        this.item = (com.allrecipes.model.YoutubeItem) wrapper.getT();
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
        Resources resources = viewHolder.context.getResources();

        adjustYoutubeImage(viewHolder, item);
    }

    private void adjustYoutubeImage(final ViewHolder holder, com.allrecipes.model.YoutubeItem item) {
            Glide.with(holder.context)
                .load(item.snippet.thumbnails.defaultThumbnail.url)
                //.placeholder(R.drawable.restaurant_placeholder)
                .into(holder.restaurantImage);
    }

    protected static class ViewHolder extends HomeScreenItem.ViewHolder {

        @BindView(R.id.videoThumbnail)
        ImageView restaurantImage;

        private final Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = itemView.getContext();
        }
    }
}
