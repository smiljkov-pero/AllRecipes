package com.allrecipes.ui.home.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.allrecipes.R;
import com.allrecipes.model.Channel;

import java.util.List;

public class ChannelsListDropdownAdapter extends ArrayAdapter<Channel> {

    private List<Channel> channels;
    private String currentChannelId;

    public ChannelsListDropdownAdapter(Context context, List<Channel> channels, String currentChannelId) {
        super(context, 0, channels);
        this.channels = channels;
        this.currentChannelId = currentChannelId;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    public void setCurrentChannelId(String currentChannelId) {
        this.currentChannelId = currentChannelId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ChannelsDropdownItemViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.home_channels_list_item, parent, false);
            viewHolder = new ChannelsDropdownItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChannelsDropdownItemViewHolder) convertView.getTag();
        }
        Channel channel = channels.get(position);
        viewHolder.initItemViewDetails(getContext(), channel, currentChannelId);

        return convertView;
    }
}
