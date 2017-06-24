package com.allrecipes.ui.home.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.Youtube;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeItem extends HomeScreenItem {

    private Youtube item;

    YoutubeItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
        this.item = (Youtube) wrapper.getT();
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
        viewHolder.name.setText(item.snippet.title);
        viewHolder.description.setText(item.snippet.description);
    }

    private void adjustYoutubeImage(final ViewHolder holder, Youtube item) {
            Glide.with(holder.context)
                .load(item.snippet.thumbnails.highThumbnail.url)
                .fitCenter()
                .centerCrop()
                //.placeholder(R.drawable.restaurant_placeholder)
                .into(holder.restaurantImage);
    }

    protected static class ViewHolder extends HomeScreenItem.ViewHolder {

        @BindView(R.id.videoThumbnail)
        ImageView restaurantImage;
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
