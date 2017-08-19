package com.allrecipes.ui.home.viewholders.items;

import android.view.View;

import com.allrecipes.R;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeAdItem extends BaseHomeScreenItem {

    public HomeAdItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
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
        final WeakReference<HomeAdItem.ViewHolder> viewHolderWeakReference = new WeakReference<>(viewHolder);
        Observable.fromCallable(new Callable<AdRequest>() {
            @Override
            public AdRequest call() throws Exception {
                AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("892B020FF18C6AB6C3F019DF8029AACE")
                    .build();
                return adRequest;
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<AdRequest>() {
                @Override
                public void accept(@NonNull AdRequest adRequest) throws Exception {
                    HomeAdItem.ViewHolder holder = viewHolderWeakReference.get();
                    if(holder != null) {
                        holder.adView.loadAd(adRequest);
                    }
                }
            });
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
