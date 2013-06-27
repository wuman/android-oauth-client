
package com.wuman.oauth.samples.plurk.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public abstract class PlurkResponse extends GenericJson {

    @Key("error_text")
    private String errorText;

    public final String getErrorText() {
        return errorText;
    }

    @Override
    public PlurkResponse clone() {
        return (PlurkResponse) super.clone();
    }

    @Override
    public PlurkResponse set(String fieldName, Object value) {
        return (PlurkResponse) super.set(fieldName, value);
    }

}
