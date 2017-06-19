package com.allrecipes.di;

import retrofit2.http.GET;

/**
 * Created by Vladimir on 11/14/2016.
 */

public interface NetworkApi {
    public static final String BASE_URL = "http://google.com";

    @GET("/test")
    void doSomeRestCall();
}
