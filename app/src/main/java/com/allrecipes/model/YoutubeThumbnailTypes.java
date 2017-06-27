package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeThumbnailTypes implements Parcelable {
    @SerializedName("default")
    public YoutubeThumbnails defaultThumbnail;

    @SerializedName("medium")
    public YoutubeThumbnails mediumThumbnail;

    @SerializedName("high")
    public YoutubeThumbnails highThumbnail;

    @SerializedName("standard")
    public YoutubeThumbnails standard;

    @SerializedName("maxres")
    public YoutubeThumbnails maxres;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.defaultThumbnail, flags);
        dest.writeParcelable(this.mediumThumbnail, flags);
        dest.writeParcelable(this.highThumbnail, flags);
        dest.writeParcelable(this.standard, flags);
        dest.writeParcelable(this.maxres, flags);
    }

    public YoutubeThumbnailTypes() {
    }

    protected YoutubeThumbnailTypes(Parcel in) {
        this.defaultThumbnail = in.readParcelable(YoutubeThumbnails.class.getClassLoader());
        this.mediumThumbnail = in.readParcelable(YoutubeThumbnails.class.getClassLoader());
        this.highThumbnail = in.readParcelable(YoutubeThumbnails.class.getClassLoader());
        this.standard = in.readParcelable(YoutubeThumbnails.class.getClassLoader());
        this.maxres = in.readParcelable(YoutubeThumbnails.class.getClassLoader());
    }

    public static final Parcelable.Creator<YoutubeThumbnailTypes> CREATOR = new Parcelable.Creator<YoutubeThumbnailTypes>() {
        @Override
        public YoutubeThumbnailTypes createFromParcel(Parcel source) {
            return new YoutubeThumbnailTypes(source);
        }

        @Override
        public YoutubeThumbnailTypes[] newArray(int size) {
            return new YoutubeThumbnailTypes[size];
        }
    };
}
