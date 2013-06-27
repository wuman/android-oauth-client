
package com.wuman.oauth.samples.twitter.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Tweet extends GenericJson {

    @Key("id_str")
    private String id;

    @Key("text")
    private String text;

    @Key("user")
    private User user;

    public final String getId() {
        return id;
    }

    public final String getText() {
        return text;
    }

    public final User getUser() {
        return user;
    }

    @Override
    public Tweet clone() {
        return (Tweet) super.clone();
    }

    @Override
    public Tweet set(String fieldName, Object value) {
        return (Tweet) super.set(fieldName, value);
    }

}
