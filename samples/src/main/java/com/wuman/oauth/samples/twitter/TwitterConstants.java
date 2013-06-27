
package com.wuman.oauth.samples.twitter;

/**
 * Constants for Twitter's OAuth implementation.
 * 
 * @author David Wu
 */
public class TwitterConstants {

    public static final String CONSUMER_KEY = "HaQvs1ZQjQGFzQgLR6rhJg";

    public static final String CONSUMER_SECRET = "T1cQGOa8kyKarwd1bmBamsKsU6A1KcEzQrjI55MoouE";

    public static final String TEMPORARY_TOKEN_REQUEST_URL = "https://api.twitter.com/oauth/request_token";

    public static final String AUTHORIZATION_VERIFIER_SERVER_URL = "https://api.twitter.com/oauth/authorize";

    public static final String TOKEN_SERVER_URL = "https://api.twitter.com/oauth/access_token";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    private TwitterConstants() {
    }

}
