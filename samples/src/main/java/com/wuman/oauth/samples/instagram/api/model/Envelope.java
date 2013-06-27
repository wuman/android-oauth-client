
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public abstract class Envelope extends GenericJson {

    @Key("meta")
    private Meta meta;

    @Key("pagination")
    private Pagination pagination;

    public final Meta getMeta() {
        return meta;
    }

    public final Pagination getPagination() {
        return pagination;
    }

    @Override
    public Envelope clone() {
        return (Envelope) super.clone();
    }

    @Override
    public Envelope set(String fieldName, Object value) {
        return (Envelope) super.set(fieldName, value);
    }

}
