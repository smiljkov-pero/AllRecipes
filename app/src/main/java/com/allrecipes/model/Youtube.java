package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class Youtube implements Parcelable {

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

    public Youtube() {
    }

    protected Youtube(Parcel in) {
        this.id = in.readParcelable(YoutubeId.class.getClassLoader());
        this.snippet = in.readParcelable(YoutubeSnipped.class.getClassLoader());
    }

    public static final Parcelable.Creator<Youtube> CREATOR = new Parcelable.Creator<Youtube>() {
        @Override
        public Youtube createFromParcel(Parcel source) {
            return new Youtube(source);
        }

        @Override
        public Youtube[] newArray(int size) {
            return new Youtube[size];
        }
    };
}
