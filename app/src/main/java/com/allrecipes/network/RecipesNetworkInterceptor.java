package com.allrecipes.network;

import android.os.Bundle;

import com.allrecipes.tracking.providers.firebase.FirebaseTracker;
import com.allrecipes.tracking.providers.firebase.NetworkRequestEvent;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RecipesNetworkInterceptor implements Interceptor {

    private static final String PARAM_URL = "url";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_RESPONSE_CODE = "response_code";
    private static final String PARAM_BYTES_SENT = "bytes_sent";
    private static final String PARAM_BYTES_RECEIVED = "bytes_received";
    private static final String PARAM_RESPONSE_TIME = "response_time";

    private final FirebaseTracker tracking;

    public RecipesNetworkInterceptor(FirebaseTracker trackingManagersProvider) {
        this.tracking = trackingManagersProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        try {
            trackNetworkEvent(request, response);
        } catch (Exception ignore) {

        }

        return response;
    }

    private void trackNetworkEvent(Request request, Response response) throws IOException {
        NetworkRequestEvent event = new NetworkRequestEvent(NetworkRequestEvent.NETWORK_EVENT);
        long time = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
        event.setUrl(request.url().toString());
        event.setResponseTime(time);
        event.setBytesReceived(response.body() != null ? response.body().contentLength() : 0L);
        event.setBytesSent(request.body() != null ? request.body().contentLength() : 0L);
        event.setMethod(request.method());
        event.setResponseCode(response.code());
        event.setException(null);
        trackNetworkEvent(event);
    }

    private void trackNetworkEvent(NetworkRequestEvent event) {
        Bundle bundle = new Bundle();
        // Here we're adding parts of the url as firebase supports a max. event param length of 42
        addUrl(bundle, event.getUrl());
        bundle.putString(PARAM_METHOD, event.getMethod());
        bundle.putInt(PARAM_RESPONSE_CODE, event.getResponseCode());
        bundle.putLong(PARAM_BYTES_SENT, event.getBytesSent());
        bundle.putLong(PARAM_BYTES_RECEIVED, event.getBytesReceived());
        bundle.putLong(PARAM_RESPONSE_TIME, event.getResponseTime());
        tracking.logEvent(event.getEventName(), bundle);
    }

    /**
     * @param bundle
     * @param url
     */
    private void addUrl(Bundle bundle, String url) {
        String urlParam = PARAM_URL;
        int part = 2;
        while (url.length() > 0) {
            bundle.putString(urlParam, url.substring(0, Math.min(url.length(), 42)));
            if (url.length() > 42) {
                url = url.substring(42, url.length());
            } else {
                url = "";
            }
            urlParam = PARAM_URL + "_part_" + part;
            part++;
        }
    }
}
