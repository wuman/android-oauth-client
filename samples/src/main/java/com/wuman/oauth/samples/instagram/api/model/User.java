
package com.wuman.oauth.samples.instagram.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class User extends GenericJson {

    @Key("id")
    private String id;

    @Key("username")
    private String username;

    @Key("full_name")
    private String fullName;

    @Key("profile_picture")
    private String profilePicture;

    public final String getId() {
        return id;
    }

    public final User setId(String id) {
        this.id = id;
        return this;
    }

    public final String getUsername() {
        return username;
    }

    public final User setUsername(String username) {
        this.username = username;
        return this;
    }

    public final String getFullName() {
        return fullName;
    }

    public final User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public final String getProfilePicture() {
        return profilePicture;
    }

    public final User setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        return this;
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
