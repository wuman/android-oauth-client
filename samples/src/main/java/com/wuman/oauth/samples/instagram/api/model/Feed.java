
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.util.Key;

import java.util.List;

public class Feed extends Envelope {

    @Key("data")
    private List<FeedItem> data;

    public final List<FeedItem> getData() {
        return data;
    }

    @Override
    public Feed clone() {
        return (Feed) super.clone();
    }

    @Override
    public Feed set(String fieldName, Object value) {
        return (Feed) super.set(fieldName, value);
    }

}
