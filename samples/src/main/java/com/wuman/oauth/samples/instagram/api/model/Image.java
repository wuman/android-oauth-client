
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Image extends GenericJson {

    @Key("url")
    private String url;

    @Key("width")
    private Integer width;

    @Key("height")
    private Integer height;

    public final String getUrl() {
        return url;
    }

    public final Integer getWidth() {
        return width;
    }

    public final Integer getHeight() {
        return height;
    }

    @Override
    public Image clone() {
        return (Image) super.clone();
    }

    @Override
    public Image set(String fieldName, Object value) {
        return (Image) super.set(fieldName, value);
    }

}
