package com.allrecipes.ui.home.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allrecipes.R;
import com.mikepenz.fastadapter.items.GenericAbstractItem;

public class HomeChannelSwipelaneItem
    extends GenericAbstractItem<HomeScreenModelItemWrapper, HomeChannelSwipelaneItem, HomeChannelSwipelaneItem.ViewHolder> {

    HomeChannelSwipelaneItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public int getType() {
        return R.id.home_swimlane_channel_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_swipelane_channel;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }
}
