package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeSnipped implements Parcelable {
    @SerializedName("publishedAt")
    public String publishedAt;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("channelTitle")
    public String channelTitle;

    @SerializedName("thumbnails")
    public YoutubeThumbnailTypes thumbnails;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.publishedAt);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.channelTitle);
        dest.writeParcelable(this.thumbnails, flags);
    }

    public YoutubeSnipped() {
    }

    protected YoutubeSnipped(Parcel in) {
        this.publishedAt = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.channelTitle = in.readString();
        this.thumbnails = in.readParcelable(YoutubeThumbnailTypes.class.getClassLoader());
    }

    public static final Parcelable.Creator<YoutubeSnipped> CREATOR = new Parcelable.Creator<YoutubeSnipped>() {
        @Override
        public YoutubeSnipped createFromParcel(Parcel source) {
            return new YoutubeSnipped(source);
        }

        @Override
        public YoutubeSnipped[] newArray(int size) {
            return new YoutubeSnipped[size];
        }
    };
}
