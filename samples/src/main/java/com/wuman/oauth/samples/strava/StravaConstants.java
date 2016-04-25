package com.wuman.oauth.samples.strava;

/**
 * Constants for Strava's OAuth implementation.
 *
 * https://strava.github.io/api/v3/oauth/
 * http://labs.strava.com/developers/
 * https://www.strava.com/settings/api
 */
public class StravaConstants {
    public static final String CLIENT_ID = "10595";
    public static final String CLIENT_SECRET = "b89afc0ae1dd798a6303e56fa86b887c7daa3d06";

    public static final String URL_AUTHORIZE = "https://www.strava.com/oauth/authorize";
    public static final String URL_TOKEN = "https://www.strava.com/oauth/token";
    public static final String REDIRECT_URL = "http://localhost/Callback";

    private StravaConstants() {
    }

}

