
package com.wuman.oauth.samples.github;

/**
 * Constants for OAuth 2.0 implementation.
 * 
 * @author David Wu
 */
public class GitHubConstants {

    public static final String CLIENT_ID = "c849afd777c81d69de30";

    public static final String CLIENT_SECRET = "cbd20b3393c58937039c9e9f19f9ad1f97abf402";

    public static final String AUTHORIZATION_CODE_SERVER_URL = "https://github.com/login/oauth/authorize";

    public static final String TOKEN_SERVER_URL = "https://github.com/login/oauth/access_token";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    private GitHubConstants() {
    }

}
