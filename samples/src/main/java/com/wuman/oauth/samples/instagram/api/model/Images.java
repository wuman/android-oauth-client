
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Images extends GenericJson {

    @Key("low_resolution")
    private Image lowResolution;

    @Key("thumbnail")
    private Image thumbnail;

    @Key("standard_resolution")
    private Image standardResolution;

    public final Image getLowResolution() {
        return lowResolution;
    }

    public final Image getThumbnail() {
        return thumbnail;
    }

    public final Image getStandardResolution() {
        return standardResolution;
    }

    @Override
    public Images clone() {
        return (Images) super.clone();
    }

    @Override
    public Images set(String fieldName, Object value) {
        return (Images) super.set(fieldName, value);
    }

}
