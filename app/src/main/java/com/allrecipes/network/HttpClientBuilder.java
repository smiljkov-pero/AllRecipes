package com.allrecipes.network;

import android.text.TextUtils;

import com.allrecipes.BuildConfig;
import com.allrecipes.managers.LocalStorageManagerInterface;
import com.allrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClientBuilder {

    static final String PARAM_API_KEY = "key";
    static final String PARAM_ACCESS_TOKEN = "access_token";

    private OkHttpClient.Builder builder;
    private Cache cache;
    private List<Interceptor> interceptors;
    private LocalStorageManagerInterface localStorageManager;

    public HttpClientBuilder(LocalStorageManagerInterface localStorageManager) {
        interceptors = new ArrayList<>();
        this.localStorageManager = localStorageManager;
    }

    private void buildDefaultQueryParameters(OkHttpClient.Builder builder) {
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                String accessToken = localStorageManager.getString(Constants.GOOGLE_LOGIN_ACCESS_TOKEN, "");
                HttpUrl url;
                if (TextUtils.isEmpty(accessToken)) {
                    url = originalHttpUrl.newBuilder()
                        .addQueryParameter(PARAM_API_KEY, BuildConfig.YOUTUBE_API_KEY)
                        .build();
                } else {
                    url = originalHttpUrl.newBuilder()
                        .addQueryParameter(PARAM_ACCESS_TOKEN, accessToken)
                        .build();
                }

                Request.Builder requestBuilder = original.newBuilder()
                    .url(url)
                    .method(original.method(), original.body());

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });
    }

    private void buildInterceptors() {
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
    }

    public HttpClientBuilder addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);

        return this;
    }

    public HttpClientBuilder setCache(Cache cache) {
        this.cache = cache;

        return this;
    }

    private void buildCache(OkHttpClient.Builder builder, Cache cache) {
        builder.cache(cache);
    }

    public OkHttpClient build() {
        if (this.builder == null) {
            builder = new OkHttpClient.Builder();
        }
        if (this.cache != null) {
            buildCache(builder, cache);
        }
        buildDefaultQueryParameters(builder);
        buildInterceptors();

        return builder.build();
    }
}
