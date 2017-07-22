package com.allrecipes.ui.home.viewholders;

import android.support.annotation.IntDef;

import com.allrecipes.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HomeScreenModelItemWrapper {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        R.id.home_screen_video_item,
        R.id.home_swimlane_channel_item
    })
    public @interface Type { }

    private Object t;
    private @Type int type;
    private Object listener;

    public HomeScreenModelItemWrapper(Object t, @Type int type) {
        this.t = t;
        this.type = type;
    }

    public HomeScreenModelItemWrapper(Object t, @Type int type, Object listener) {
        this.t = t;
        this.type = type;
        this.listener = listener;
    }

    public Object getT() {
        return t;
    }

    public void setT(Object t) {
        this.t = t;
    }

    @Type
    public int getType() {
        return type;
    }

    public void setType(@Type int type) {
        this.type = type;
    }

    public Object getListener(){
        return listener;
    }
}
