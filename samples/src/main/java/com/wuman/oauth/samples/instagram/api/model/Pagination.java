
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Pagination extends GenericJson {

    @Key("next_url")
    private String nextUrl;

    @Key("next_max_id")
    private String nextMaxId;

    public final String getNextUrl() {
        return nextUrl;
    }

    public final String getNextMaxId() {
        return nextMaxId;
    }

    @Override
    public Pagination clone() {
        return (Pagination) super.clone();
    }

    @Override
    public Pagination set(String fieldName, Object value) {
        return (Pagination) super.set(fieldName, value);
    }

}
