package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeFilterOption implements Parcelable {

    private String recipeFilter;
    private boolean isChecked;

    public RecipeFilterOption(String restaurantCharacteristic, boolean isChecked) {
        this.recipeFilter = restaurantCharacteristic;
        this.isChecked = isChecked;
    }

    // endregion

    // region getter and setter

    public String getRecipeFilter() {
        return recipeFilter;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    // endregion

    // region equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        String that = (String) o;

        return recipeFilter.equals(that);

    }

    @Override
    public int hashCode() {
        return recipeFilter.hashCode();
    }


    // endregion

    // region Parcelable

    protected RecipeFilterOption(Parcel in) {
        recipeFilter = (String) in.readValue(String.class.getClassLoader());
        isChecked = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(recipeFilter);
        dest.writeByte((byte) (isChecked ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Creator<RecipeFilterOption> CREATOR = new Creator<RecipeFilterOption>() {
        @Override
        public RecipeFilterOption createFromParcel(Parcel in) {
            return new RecipeFilterOption(in);
        }

        @Override
        public RecipeFilterOption[] newArray(int size) {
            return new RecipeFilterOption[size];
        }
    };

    // endregion
}
