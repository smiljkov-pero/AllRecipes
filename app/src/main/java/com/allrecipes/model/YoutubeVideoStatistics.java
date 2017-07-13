package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class YoutubeVideoStatistics implements Parcelable {

    @SerializedName("viewCount")
    public String viewCount;

    @SerializedName("likeCount")
    public String likeCount;

    @SerializedName("dislikeCount")
    public String dislikeCount;

    @SerializedName("favoriteCount")
    public String favoriteCount;

    @SerializedName("commentCount")
    public String commentCount;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.viewCount);
        dest.writeString(this.likeCount);
        dest.writeString(this.dislikeCount);
        dest.writeString(this.favoriteCount);
        dest.writeString(this.commentCount);
    }

    public YoutubeVideoStatistics() {
    }

    protected YoutubeVideoStatistics(Parcel in) {
        this.viewCount = in.readString();
        this.likeCount = in.readString();
        this.dislikeCount = in.readString();
        this.favoriteCount = in.readString();
        this.commentCount = in.readString();
    }

    public static final Creator<YoutubeVideoStatistics> CREATOR = new Creator<YoutubeVideoStatistics>() {
        @Override
        public YoutubeVideoStatistics createFromParcel(Parcel source) {
            return new YoutubeVideoStatistics(source);
        }

        @Override
        public YoutubeVideoStatistics[] newArray(int size) {
            return new YoutubeVideoStatistics[size];
        }
    };
}
