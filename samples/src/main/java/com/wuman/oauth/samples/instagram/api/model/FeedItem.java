
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;

public class FeedItem extends GenericJson {

    @Key("type")
    private String type;

    @Key("id")
    private String id;

    @JsonString
    @Key("created_time")
    private Long createdTime;

    @Key("images")
    private Images images;

    @Key("user")
    private User user;

    public final String getType() {
        return type;
    }

    public final String getId() {
        return id;
    }

    public final Long getCreatedTime() {
        return createdTime;
    }

    public final Images getImages() {
        return images;
    }

    public final User getUser() {
        return user;
    }

    @Override
    public FeedItem clone() {
        return (FeedItem) super.clone();
    }

    @Override
    public FeedItem set(String fieldName, Object value) {
        return (FeedItem) super.set(fieldName, value);
    }

}
