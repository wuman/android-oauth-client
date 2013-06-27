
package com.wuman.oauth.samples.twitter.api.model;

import com.google.api.client.util.Key;

import java.util.List;

public class Timeline extends TwitterResponse {

    @Key(KEY_DATA)
    private List<Tweet> tweets;

    public final List<Tweet> getTweets() {
        return tweets;
    }

    @Override
    public Timeline clone() {
        return (Timeline) super.clone();
    }

    @Override
    public Timeline set(String fieldName, Object value) {
        return (Timeline) super.set(fieldName, value);
    }

}
