package com.allrecipes.ui.home.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
    @BindView(R.id.channelName)
    TextView channelName;
    @BindView(R.id.description)
    TextView description;

    public ChannelsDropdownItemViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void initItemViewDetails(Context context, Channel channel, boolean isChecked) {
        channelName.setText(channel.getName());
        description.setText(channel.getDescription());
        Picasso.with(context)
                .load(channel.getImage())
                //.placeholder(R.drawable.restaurant_placeholder)
                .config(Bitmap.Config.RGB_565)
                .into(channelImage);
    }
}
