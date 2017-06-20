package com.allrecipes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PeroSmiljkov on 20.06.17.
 */

public class PageInfo {
    @SerializedName("totalResults")
    public int totalResults;

    @SerializedName("resultsPerPage")
    public int resultsPerPage;
}
