package com.allrecipes.ui.home.viewholders.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdItem extends BaseHomeScreenItem {

    public HomeAdItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public int getType() {
        return R.id.item_home_ad;
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

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        viewHolder.adView.loadAd(adRequest);
    }

    protected static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.adView)
        AdView adView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
