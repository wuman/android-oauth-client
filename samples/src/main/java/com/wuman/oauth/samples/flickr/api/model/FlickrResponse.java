
package com.wuman.oauth.samples.flickr.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;

public abstract class FlickrResponse extends GenericJson {

    @Key("stat")
    private String stat;

    @JsonString
    @Key("code")
    private Integer errorCode;

    @Key("message")
    private String errorMessage;

    public final String getStat() {
        return stat;
    }

    public final Integer getErrorCode() {
        return errorCode;
    }

    public final String getErrorMessage() {
        return errorMessage;
    }

}
