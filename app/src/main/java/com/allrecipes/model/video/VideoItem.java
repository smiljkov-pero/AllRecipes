package com.allrecipes.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.allrecipes.model.ContentDetails;
import com.allrecipes.model.YoutubeId;
import com.allrecipes.model.YoutubeSnipped;
import com.allrecipes.model.YoutubeVideoStatistics;
import com.google.gson.annotations.SerializedName;

public class VideoItem implements Parcelable {

    @SerializedName("id")
    public String id;

    @SerializedName("snippet")
    public YoutubeSnipped snippet;

    @SerializedName("contentDetails")
    public ContentDetails contentDetails;

    @SerializedName("statistics")
    public YoutubeVideoStatistics statistics;

    public VideoItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.snippet, flags);
        dest.writeParcelable(this.contentDetails, flags);
        dest.writeParcelable(this.statistics, flags);
    }

    protected VideoItem(Parcel in) {
        this.id = in.readString();
        this.snippet = in.readParcelable(YoutubeSnipped.class.getClassLoader());
        this.contentDetails = in.readParcelable(ContentDetails.class.getClassLoader());
        this.statistics = in.readParcelable(YoutubeVideoStatistics.class.getClassLoader());
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel source) {
            return new VideoItem(source);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };
}
