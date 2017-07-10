package com.allrecipes.ui.home.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allrecipes.R;
import com.mikepenz.fastadapter.items.GenericAbstractItem;

public class BaseHomeScreenItem
    extends GenericAbstractItem<HomeScreenModelItemWrapper, BaseHomeScreenItem, BaseHomeScreenItem.ViewHolder> {

    public BaseHomeScreenItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
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
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }
}
