package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeThumbnails implements Parcelable {
    @SerializedName("url")
    public String url;

    @SerializedName("width")
    public String width;

    @SerializedName("height")
    public String height;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.width);
        dest.writeString(this.height);
    }

    public YoutubeThumbnails() {
    }

    protected YoutubeThumbnails(Parcel in) {
        this.url = in.readString();
        this.width = in.readString();
        this.height = in.readString();
    }

    public static final Parcelable.Creator<YoutubeThumbnails> CREATOR = new Parcelable.Creator<YoutubeThumbnails>() {
        @Override
        public YoutubeThumbnails createFromParcel(Parcel source) {
            return new YoutubeThumbnails(source);
        }

        @Override
        public YoutubeThumbnails[] newArray(int size) {
            return new YoutubeThumbnails[size];
        }
    };
}
