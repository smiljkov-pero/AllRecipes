package com.allrecipes.tracking.providers.firebase;

import android.support.v4.util.ArrayMap;

import java.util.Map;

public abstract class TrackingEvent {
    protected String eventName;
    protected String value;
    protected Map<String, String> parameters;

    public TrackingEvent(String eventName) {
        this.eventName = eventName;
        this.parameters = new ArrayMap<>();
    }

    public String getEventName() {
        return eventName;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
