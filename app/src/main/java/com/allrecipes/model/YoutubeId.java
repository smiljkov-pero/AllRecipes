package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class YoutubeId implements Parcelable {

    @SerializedName("kind")
    public String kind;

    @SerializedName("videoId")
    public String videoId;

    @SerializedName("channelId")
    public String channelId;

    @SerializedName("playlistId")
    public String playlistId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kind);
        dest.writeString(this.videoId);
        dest.writeString(this.channelId);
        dest.writeString(this.playlistId);
    }

    public YoutubeId() {
    }

    public YoutubeId(String videoId) {
        this.videoId = videoId;
    }

    protected YoutubeId(Parcel in) {
        this.kind = in.readString();
        this.videoId = in.readString();
        this.channelId = in.readString();
        this.playlistId = in.readString();
    }

    public static final Parcelable.Creator<YoutubeId> CREATOR = new Parcelable.Creator<YoutubeId>() {
        @Override
        public YoutubeId createFromParcel(Parcel source) {
            return new YoutubeId(source);
        }

        @Override
        public YoutubeId[] newArray(int size) {
            return new YoutubeId[size];
        }
    };
}
