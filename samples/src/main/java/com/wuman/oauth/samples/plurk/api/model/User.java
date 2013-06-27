
package com.wuman.oauth.samples.plurk.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class User extends GenericJson {

    @Key("id")
    private Long id;

    @Key("display_name")
    private String displayName;

    @Key("nick_name")
    private String nickName;

    @Key("has_profile_image")
    private Integer hasProfileImage;

    @Key("avatar")
    private Integer avatar;

    public final Long getId() {
        return id;
    }

    public final String getDisplayName() {
        return displayName;
    }

    public final String getNickName() {
        return nickName;
    }

    public final Boolean getHasProfileImage() {
        return hasProfileImage == 1;
    }

    public final Integer getAvatar() {
        return avatar;
    }

    private String profileImage = null;

    public final String getProfileImage() {
        if (profileImage != null) {
            return profileImage;
        }

        if (getHasProfileImage()) {
            if (getAvatar() != null && getAvatar() != 0) {
                profileImage = String.format("http://avatars.plurk.com/%s-medium%s.gif", getId(),
                        getAvatar());
            } else {
                profileImage = String.format("http://avatars.plurk.com/%s-medium.gif", getId());
            }
        } else {
            profileImage = "http://www.plurk.com/static/default_medium.gif";
        }
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
