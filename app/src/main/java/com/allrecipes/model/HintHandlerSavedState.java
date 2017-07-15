package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HintHandlerSavedState implements Parcelable {

    public final int hintLeftPadding, hintBottomPadding;
    public final boolean shouldKeepSpaceForHint, hintImprovementUsed, drawAtStartOfParent;

    public HintHandlerSavedState(int hintLeftPadding, int hintBottomPadding, boolean shouldKeepSpaceForHint, boolean hintImprovementUsed, boolean drawAtStartOfParent) {
        this.hintBottomPadding = hintBottomPadding;
        this.hintLeftPadding = hintLeftPadding;
        this.shouldKeepSpaceForHint = shouldKeepSpaceForHint;
        this.hintImprovementUsed = hintImprovementUsed;
        this.drawAtStartOfParent = drawAtStartOfParent;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(shouldKeepSpaceForHint ? 1 : 0);
        out.writeInt(hintImprovementUsed ? 1 : 0);
        out.writeInt(drawAtStartOfParent ? 1 : 0);
        out.writeInt(hintLeftPadding);
        out.writeInt(hintBottomPadding);
    }

    public static final Creator<HintHandlerSavedState> CREATOR = new Creator<HintHandlerSavedState>() {
        public HintHandlerSavedState createFromParcel(Parcel in) {
            return new HintHandlerSavedState(in);
        }

        public HintHandlerSavedState[] newArray(int size) {
            return new HintHandlerSavedState[size];
        }
    };

    private HintHandlerSavedState(Parcel in) {
        shouldKeepSpaceForHint = in.readInt() == 1;
        hintImprovementUsed = in.readInt() == 1;
        drawAtStartOfParent = in.readInt() == 1;
        hintLeftPadding = in.readInt();
        hintBottomPadding = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
