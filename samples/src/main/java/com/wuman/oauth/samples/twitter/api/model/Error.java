
package com.wuman.oauth.samples.twitter.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Error extends GenericJson {

    @Key("code")
    private Integer code;

    @Key("message")
    private String message;

    public final Integer getCode() {
        return code;
    }

    public final String getMessage() {
        return message;
    }

    @Override
    public Error clone() {
        return (Error) super.clone();
    }

    @Override
    public Error set(String fieldName, Object value) {
        return (Error) super.set(fieldName, value);
    }

}
