package com.allrecipes.ui.home.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.allrecipes.R;

import java.util.List;

public class ChannelsListDropdownAdapter<T extends String> extends ArrayAdapter<T> {

    private final List<T> addresses;
    private final int selectedPosition;

    public ChannelsListDropdownAdapter(Context context, List<T> addressesObjects, int selectedPosition) {
        super(context, 0, addressesObjects);
        this.addresses = addressesObjects;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getCount() {
        return addresses.size();
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
        T addressObj = addresses.get(position);


        return convertView;
    }
}
