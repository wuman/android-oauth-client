
package com.wuman.oauth.samples.flickr;

import android.text.TextUtils;

/**
 * Constants for Flickr's OAuth 1.0a implementation.
 * 
 * @author David Wu
 */
public class FlickrConstants {

    public static final String CONSUMER_KEY = "8b5550b0b94aaf0ed1f5a8bae7fec172";

    public static final String CONSUMER_SECRET = "e85a136e27a18a7a";

    public static final String TEMPORARY_TOKEN_REQUEST_URL = "http://m.flickr.com/services/oauth/request_token";

    public static final String AUTHORIZATION_VERIFIER_SERVER_URL = "http://m.flickr.com/services/oauth/authorize";

    public static final String TOKEN_SERVER_URL = "http://m.flickr.com/services/oauth/access_token";

    public static final String REDIRECT_URL = "http://localhost/Callback";

    public static final String generateThumbnailPhotoUrl(int farm, String server, String photoId,
            String secret) {
        return generatePhotoUrl("t", farm, server, photoId, secret);
    }

    public static final String generateSmallPhotoUrl(int farm, String server, String photoId,
            String secret) {
        return generatePhotoUrl("n", farm, server, photoId, secret);
    }

    public static final String generateMediumPhotoUrl(int farm, String server, String photoId,
            String secret) {
        return generatePhotoUrl("z", farm, server, photoId, secret);
    }

    public static final String generateLargePhotoUrl(int farm, String server, String photoId,
            String secret) {
        return generatePhotoUrl("b", farm, server, photoId, secret);
    }

    private static String generatePhotoUrl(String suffix, int farm, String server, String photoId,
            String secret) {
        return String.format("http://farm%s.staticflickr.com/%s/%s_%s_%s.jpg", farm, server,
                photoId, secret, suffix);
    }

    public static final String generateBuddyIcon(int iconFarm, String iconServer, String nsid) {
        if (TextUtils.isEmpty(nsid) || TextUtils.isEmpty(iconServer)
                || TextUtils.equals(iconServer, "0")) {
            return "http://www.flickr.com/images/buddyicon.gif";
        } else {
            return String.format("http://farm%s.staticflickr.com/%s/buddyicons/%s.jpg", iconFarm,
                    iconServer, nsid);
        }
    }

    private FlickrConstants() {
    }

}
