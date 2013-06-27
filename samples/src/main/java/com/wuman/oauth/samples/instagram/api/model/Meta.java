
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Meta extends GenericJson {

    @Key("code")
    private Integer code;

    @Key("error_type")
    private String errorType;

    @Key("error_message")
    private String errorMessage;

    public final Integer getCode() {
        return code;
    }

    public final String getErrorType() {
        return errorType;
    }

    public final String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Meta clone() {
        return (Meta) super.clone();
    }

    @Override
    public Meta set(String fieldName, Object value) {
        return (Meta) super.set(fieldName, value);
    }

}
