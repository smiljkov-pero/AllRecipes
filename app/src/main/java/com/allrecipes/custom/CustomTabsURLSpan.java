package com.allrecipes.custom;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.support.customtabs.CustomTabsIntent;
import android.text.style.URLSpan;
import android.view.View;

public class CustomTabsURLSpan extends URLSpan {
    private CustomTabsIntent mCustomTabsIntent;
    private Context context;

    public CustomTabsURLSpan(String url, CustomTabsIntent mCustomTabsIntent, Context context) {
        super(url);
        this.mCustomTabsIntent = mCustomTabsIntent;
        this.context = context;
    }

    public CustomTabsURLSpan(Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {
        String url = getURL();
        mCustomTabsIntent.launchUrl(context, Uri.parse(url));
        // attempt to open with custom tabs, if that fails, call super.onClick
    }
}