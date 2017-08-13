package com.allrecipes.network;

import android.text.TextUtils;

import com.allrecipes.model.YoutubeId;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class YoutubeItemIdDeserializer implements JsonDeserializer<YoutubeId> {

    @Override
    public YoutubeId deserialize(
        JsonElement json,
        Type type,
        JsonDeserializationContext context
    ) throws JsonParseException {
        YoutubeId port = new YoutubeId();

        if (json.isJsonObject()) {
            port.kind = json.getAsJsonObject().get("kind").getAsString();
            if (TextUtils.equals(port.kind, "youtube#video")) {
                port.videoId = json.getAsJsonObject().get("videoId").getAsString();
            }
        }

        return port;
    }
}
