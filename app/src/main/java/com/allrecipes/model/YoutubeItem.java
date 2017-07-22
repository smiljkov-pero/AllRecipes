package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class YoutubeItem implements Parcelable {

    @SerializedName("id")
    public YoutubeId id;

    @SerializedName("snippet")
    public YoutubeSnipped snippet;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.id, flags);
        dest.writeParcelable(this.snippet, flags);
    }

    public YoutubeItem() {
    }

    protected YoutubeItem(Parcel in) {
        this.id = in.readParcelable(YoutubeId.class.getClassLoader());
        this.snippet = in.readParcelable(YoutubeSnipped.class.getClassLoader());
    }

    public static final Parcelable.Creator<YoutubeItem> CREATOR = new Parcelable.Creator<YoutubeItem>() {
        @Override
        public YoutubeItem createFromParcel(Parcel source) {
            return new YoutubeItem(source);
        }

        @Override
        public YoutubeItem[] newArray(int size) {
            return new YoutubeItem[size];
        }
    };

    public YoutubeItem(YoutubeId id, YoutubeSnipped snippet) {
        this.id = id;
        this.snippet = snippet;
    }

    public YoutubeItem(YoutubeSnipped snippet) {
        this.snippet = snippet;
    }
}
