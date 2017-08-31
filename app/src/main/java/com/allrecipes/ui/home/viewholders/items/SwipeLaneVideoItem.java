package com.allrecipes.ui.home.viewholders.items;

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

public class SwipeLaneVideoItem extends BaseHomeScreenItem {

    private YoutubeItem item;
    private final NetworkQuality networkQuality;

    public SwipeLaneVideoItem(HomeScreenModelItemWrapper wrapper, NetworkQuality networkQuality) {
        super(wrapper);
        this.item = (YoutubeItem) wrapper.getT();
        this.networkQuality = networkQuality;
    }

    public YoutubeItem getItem() {
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

    private void adjustYoutubeImage(final SwipeLaneVideoItem.ViewHolder holder, YoutubeItem item) {
        if (item.snippet != null && item.snippet.thumbnails != null) {
            String url = networkQuality.getNetworkQualityCoefficient() > 0.5
                ? item.snippet.thumbnails.highThumbnail.url
                : item.snippet.thumbnails.mediumThumbnail.url;

            Picasso.with(holder.videoThumbnail.getContext())
                .load(url)
                .fit()
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .into(holder.videoThumbnail);
        }
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
