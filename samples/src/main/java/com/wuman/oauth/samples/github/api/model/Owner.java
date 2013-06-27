
package com.wuman.oauth.samples.github.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Owner extends GenericJson {

    @Key("login")
    private String login;

    @Key("id")
    private Long id;

    @Key("avatar_url")
    private String avatarUrl;

    public final String getLogin() {
        return login;
    }

    public final Long getId() {
        return id;
    }

    public final String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public Owner clone() {
        return (Owner) super.clone();
    }

    @Override
    public Owner set(String fieldName, Object value) {
        return (Owner) super.set(fieldName, value);
    }

}
