package com.allrecipes.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AllRecipesNetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String string = request.url().toString();
        string = string.replace("%26", "&");
        string = string.replace("%3D", "=");

        Request newRequest = new Request.Builder()
            .url(string)
            .build();

        return chain.proceed(newRequest);
    }
}
