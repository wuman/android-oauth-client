
package com.wuman.oauth.samples.instagram;

/**
 * Constants for Instagram's OAuth 2.0 implementation.
 * 
 * @author David Wu
 */
public class InstagramConstants {

    public static final String CLIENT_ID = "d5f978f82a7d41088a4c550cf3d981a8";

    public static final String AUTHORIZATION_CODE_SERVER_URL = "https://api.instagram.com/oauth/authorize";

    public static final String AUTHORIZATION_IMPLICIT_SERVER_URL = "https://api.instagram.com/oauth/authorize";

    public static final String TOKEN_SERVER_URL = "https://api.instagram.com/oauth/access_token";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    private InstagramConstants() {
    }

}
