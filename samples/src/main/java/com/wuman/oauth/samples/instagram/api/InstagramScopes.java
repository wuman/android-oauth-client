
package com.wuman.oauth.samples.instagram.api;

public class InstagramScopes {

    /**
     * to read any and all data related to a user (e.g. following/followed-by
     * lists, photos, etc.) (granted by default)
     */
    public static final String BASIC = "basic";

    /** to create or delete comments on a user’s behalf */
    public static final String COMMENTS = "comments";

    /** to follow and unfollow users on a user’s behalf */
    public static final String RELATIONSHIPS = "relationships";

    /** to like and unlike items on a user’s behalf */
    public static final String LIKES = "likes";

    private InstagramScopes() {
    }

}
