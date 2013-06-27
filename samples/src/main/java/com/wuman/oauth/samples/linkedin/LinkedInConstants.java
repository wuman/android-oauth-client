
package com.wuman.oauth.samples.linkedin;

/**
 * Constants for LinkedIn's OAuth implementation.
 * 
 * @author David Wu
 */
public class LinkedInConstants {

    public static final String CLIENT_ID = "bsvvqh4g9k54";

    public static final String CLIENT_SECRET = "DHfxBqAeZFnAHDvJ";

    public static final String TEMPORARY_TOKEN_REQUEST_URL = "https://api.linkedin.com/uas/oauth/requestToken";

    private static final String AUTHORIZATION_REQUEST_STATE = "state";

    public static final String AUTHORIZATION_VERIFIER_SERVER_URL = "https://api.linkedin.com/uas/oauth/authenticate";

    public static final String AUTHORIZATION_CODE_SERVER_URL = "https://www.linkedin.com/uas/oauth2/authorization?state="
            + AUTHORIZATION_REQUEST_STATE;

    public static final String OAUTH_TOKEN_SERVER_URL = "https://api.linkedin.com/uas/oauth/accessToken";

    public static final String OAUTH2_TOKEN_SERVER_URL = "https://www.linkedin.com/uas/oauth2/accessToken";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    private LinkedInConstants() {
    }

}
