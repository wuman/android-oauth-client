
package com.wuman.oauth.samples.twitter.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.List;

public abstract class TwitterResponse extends GenericJson {

    public static final String KEY_DATA = "data";

    @Key("errors")
    private List<Error> errors;

    public final List<Error> getErrors() {
        return errors;
    }

    @Override
    public TwitterResponse clone() {
        return (TwitterResponse) super.clone();
    }

    @Override
    public TwitterResponse set(String fieldName, Object value) {
        return (TwitterResponse) super.set(fieldName, value);
    }

}
