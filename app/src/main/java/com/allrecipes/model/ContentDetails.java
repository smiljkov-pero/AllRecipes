package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ContentDetails implements Parcelable {

    @SerializedName("duration")
    public String duration;

    @SerializedName("dimension")
    public String dimension;

    @SerializedName("definition")
    public String definition;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.duration);
        dest.writeString(this.dimension);
        dest.writeString(this.definition);
    }

    public ContentDetails() {
    }

    protected ContentDetails(Parcel in) {
        this.duration = in.readString();
        this.dimension = in.readString();
        this.definition = in.readString();
    }

    public static final Parcelable.Creator<ContentDetails> CREATOR = new Parcelable.Creator<ContentDetails>() {
        @Override
        public ContentDetails createFromParcel(Parcel source) {
            return new ContentDetails(source);
        }

        @Override
        public ContentDetails[] newArray(int size) {
            return new ContentDetails[size];
        }
    };
}
