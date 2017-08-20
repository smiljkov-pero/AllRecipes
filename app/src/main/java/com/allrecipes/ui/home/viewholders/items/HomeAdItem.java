package com.allrecipes.ui.home.viewholders.items;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allrecipes.R;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdItem extends BaseHomeScreenItem {

    private NativeExpressAdView item;

    public HomeAdItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
        this.item = (NativeExpressAdView) wrapper.getT();
    }

    @Override
    public int getType() {
        return R.id.home_ad_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ad_home;
    }

    @Override
    public HomeAdItem.ViewHolder getViewHolder(View v) {
        return new HomeAdItem.ViewHolder(v);
    }

    @Override
    public void bindView(BaseViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        HomeAdItem.ViewHolder viewHolder = ((HomeAdItem.ViewHolder) holder);
        LinearLayout adViewParent = viewHolder.adViewParent;
        adViewParent.removeAllViews();

        if (item.getParent() != null) {
            ((LinearLayout) item.getParent()).removeView(item);
        }
        viewHolder.adViewParent.addView(item);
    }

    protected static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.adViewParent)
        LinearLayout adViewParent;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
