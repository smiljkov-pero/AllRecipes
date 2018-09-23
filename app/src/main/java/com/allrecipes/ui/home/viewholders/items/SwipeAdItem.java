package com.allrecipes.ui.home.viewholders.items;

import android.view.View;

import com.allrecipes.R;
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem;
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;

public class SwipeAdItem extends BaseHomeScreenItem {

    public SwipeAdItem(HomeScreenModelItemWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public int getType() {
        return R.id.swipelane_ad_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ad_swipe_lane;
    }

    @Override
    public SwipeAdItem.ViewHolder getViewHolder(View v) {
        return new SwipeAdItem.ViewHolder(v);
    }

    @Override
    public void bindView(BaseViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        SwipeAdItem.ViewHolder viewHolder = ((SwipeAdItem.ViewHolder) holder);
        final WeakReference<SwipeAdItem.ViewHolder> viewHolderWeakReference = new WeakReference<>(viewHolder);

        /*Observable.fromCallable(new Callable<AdRequest>() {
            @Override
            public AdRequest call() throws Exception {
                AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("892B020FF18C6AB6C3F019DF8029AACE")
                    .build();
                return adRequest;
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<AdRequest>() {
                @Override
                public void accept(@NonNull AdRequest adRequest) throws Exception {
                    SwipeAdItem.ViewHolder holder = viewHolderWeakReference.get();
                    if (holder != null) {
                        holder.adView.loadAd(adRequest);
                    }
                }
            });*/
    }

    protected static class ViewHolder extends BaseViewHolder {

        /*@BindView(R.id.adView)
        NativeExpressAdView adView;*/

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
