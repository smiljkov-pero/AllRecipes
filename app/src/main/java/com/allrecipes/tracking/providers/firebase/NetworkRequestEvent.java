package com.allrecipes.tracking.providers.firebase;

public class NetworkRequestEvent extends TrackingEvent {

    public static final String NETWORK_EVENT = "network_event";
    private String url;
    private String method;
    private int responseCode;
    private long responseTime;
    private long bytesSent;
    private long bytesReceived;
    private Throwable exception;

    public NetworkRequestEvent(String eventName) {
        super(eventName);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
