
package com.wuman.oauth.samples.twitter.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class User extends GenericJson {

    @Key("id")
    private Integer id;

    @Key("name")
    private String name;

    @Key("profile_image_url_https")
    private String profileImage;

    public final Integer getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final String getProfileImage() {
        return profileImage;
    }

    @Override
    public User clone() {
        return (User) super.clone();
    }

    @Override
    public User set(String fieldName, Object value) {
        return (User) super.set(fieldName, value);
    }

}
