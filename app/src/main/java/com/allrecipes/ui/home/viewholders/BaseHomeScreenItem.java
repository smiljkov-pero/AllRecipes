package com.allrecipes.ui.home.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allrecipes.R;
import com.mikepenz.fastadapter.items.GenericAbstractItem;

public class BaseHomeScreenItem
    extends GenericAbstractItem<HomeScreenModelItemWrapper, BaseHomeScreenItem, BaseHomeScreenItem.BaseViewHolder> {

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
    public BaseViewHolder getViewHolder(View v) {
        return new BaseViewHolder(v);
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View view) {
            super(view);
        }
    }
}
