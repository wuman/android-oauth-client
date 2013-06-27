
package com.wuman.oauth.samples.flickr.api.model;

import com.google.api.client.util.Key;

public class ContactsPhotos extends FlickrResponse {

    @Key("photos")
    private Photos photos;

    public final Photos getPhotos() {
        return photos;
    }

    @Override
    public ContactsPhotos clone() {
        return (ContactsPhotos) super.clone();
    }

    @Override
    public ContactsPhotos set(String fieldName, Object value) {
        return (ContactsPhotos) super.set(fieldName, value);
    }

}
