package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class YoutubeSnipped implements Parcelable {

    @SerializedName("publishedAt")
    public Date publishedAt;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("channelTitle")
    public String channelTitle;

    @SerializedName("channelId")
    public String channelId;

    @SerializedName("tags")
    public List<String> tags;

    @SerializedName("thumbnails")
    public YoutubeThumbnailTypes thumbnails;

    @SerializedName("categoryId")
    public long categoryId;

    @SerializedName("resourceId")
    public YoutubeId resourceId;

    public YoutubeSnipped() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.publishedAt != null ? this.publishedAt.getTime() : -1);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.channelTitle);
        dest.writeString(this.channelId);
        dest.writeStringList(this.tags);
        dest.writeParcelable(this.thumbnails, flags);
        dest.writeLong(this.categoryId);
        dest.writeParcelable(this.resourceId, flags);
    }

    protected YoutubeSnipped(Parcel in) {
        long tmpPublishedAt = in.readLong();
        this.publishedAt = tmpPublishedAt == -1 ? null : new Date(tmpPublishedAt);
        this.title = in.readString();
        this.description = in.readString();
        this.channelTitle = in.readString();
        this.channelId = in.readString();
        this.tags = in.createStringArrayList();
        this.thumbnails = in.readParcelable(YoutubeThumbnailTypes.class.getClassLoader());
        this.categoryId = in.readLong();
        this.resourceId = in.readParcelable(YoutubeId.class.getClassLoader());
    }

    public static final Creator<YoutubeSnipped> CREATOR = new Creator<YoutubeSnipped>() {
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
