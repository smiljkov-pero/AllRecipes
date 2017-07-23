package com.allrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FiltersAndSortSettings implements Parcelable {

    private List<RecipeFilterOption> filters;
    private String sort;

    public FiltersAndSortSettings() {
        filters = new ArrayList<>();
    }

    public static boolean isAtLeastOneFilterOptionSet(FiltersAndSortSettings filtersAndSortSettings) {
        return isAtLeastOneFilterSet(filtersAndSortSettings);
    }

    public static boolean isAtLeastOneFilterSet(FiltersAndSortSettings filtersAndSortSettings) {
        for (RecipeFilterOption filterOption : filtersAndSortSettings.getFilters()) {
            if (filterOption.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public List<RecipeFilterOption> getFilters() {
        return filters;
    }

    public void setFilters(List<RecipeFilterOption> filters) {
        this.filters = filters;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.filters);
        dest.writeString(this.sort);
    }

    protected FiltersAndSortSettings(Parcel in) {
        this.filters = in.createTypedArrayList(RecipeFilterOption.CREATOR);
        this.sort = in.readString();
    }

    public static final Creator<FiltersAndSortSettings> CREATOR = new Creator<FiltersAndSortSettings>() {
        @Override
        public FiltersAndSortSettings createFromParcel(Parcel source) {
            return new FiltersAndSortSettings(source);
        }

        @Override
        public FiltersAndSortSettings[] newArray(int size) {
            return new FiltersAndSortSettings[size];
        }
    };
}
