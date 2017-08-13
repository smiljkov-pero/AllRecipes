package com.allrecipes.ui.home.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.Channel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelsDropdownItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.channelImage)
    ImageView channelImage;
    @BindView(R.id.selectedChannelIcon)
    ImageView selectedChannelIcon;
    @BindView(R.id.channelName)
    TextView channelName;
    @BindView(R.id.description)
    TextView description;

    public ChannelsDropdownItemViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void initItemViewDetails(Context context, Channel channel, String currentChannelId) {
        channelName.setText(channel.getName());
        description.setText(channel.getDescription());
        selectedChannelIcon
            .setVisibility(TextUtils.equals(channel.getChannelId(), currentChannelId) ? View.VISIBLE : View.GONE);
        Picasso.with(context)
                .load(channel.getImage())
                .config(Bitmap.Config.RGB_565)
                .into(channelImage);
    }
}
