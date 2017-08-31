package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.YoutubeItem;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.foodora.android.networkutils.NetworkQuality;

public class YoutubeVideoItem extends BaseHomeScreenItem {

    private YoutubeItem item;
    private final NetworkQuality networkQuality;

    public YoutubeVideoItem(HomeScreenModelItemWrapper wrapper, NetworkQuality networkQuality) {
        super(wrapper);
        this.item = (YoutubeItem) wrapper.getT();
        this.networkQuality = networkQuality;
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
    public YoutubeVideoItem.ViewHolder getViewHolder(View v) {
        return new YoutubeVideoItem.ViewHolder(v);
    }

    @Override
    public void bindView(BaseViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        YoutubeVideoItem.ViewHolder viewHolder = ((YoutubeVideoItem.ViewHolder) holder);

        adjustYoutubeImage(viewHolder, item);
        viewHolder.name.setText(item.snippet.title);
        viewHolder.description.setText(item.snippet.description);
    }

    private void adjustYoutubeImage(final ViewHolder holder, YoutubeItem item) {
        String url = networkQuality.getNetworkQualityCoefficient() > 0.5
            ? item.snippet.thumbnails.highThumbnail.url
            : item.snippet.thumbnails.mediumThumbnail.url;
        Picasso.with(holder.context)
            .load(url)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_item_placeholder)
            .config(Bitmap.Config.RGB_565)
            .into(holder.videoThumbnail);
    }

    protected static class ViewHolder extends BaseViewHolder {

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
