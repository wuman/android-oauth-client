
package com.wuman.oauth.samples.foursquare;

/**
 * Constants for foursquare's 2.0 implementation.
 * 
 * @author David Wu
 */
public class FoursquareConstants {

    public static final String CLIENT_ID = "VH0JK02YYUVQKZV5L4BH3PYIJ5HMAWSP44KC3FU45QBG1BXJ";

    public static final String CLIENT_SECRET = "ESNUTENGZB4Y31PKXM2GIQQMXPAG2TCLIRTFJ3QTV1YJ4F14";

    public static final String AUTHORIZATION_CODE_SERVER_URL = "https://foursquare.com/oauth2/authorize";

    public static final String AUTHORIZATION_IMPLICIT_SERVER_URL = "https://foursquare.com/oauth2/authenticate";

    public static final String TOKEN_SERVER_URL = "https://foursquare.com/oauth2/access_token";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    private FoursquareConstants() {
    }

}
