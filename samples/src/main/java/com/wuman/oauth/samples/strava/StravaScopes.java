package com.wuman.oauth.samples.strava;

/**
 * Access scopes
 *
 * "public" default, private activities are not returned, privacy zones are respected in stream requests
 * "view_private" view private activities and data within privacy zones
 * "write" modify activities, upload on the user’s behalf
 * "view_private,write" both ‘view_private’ and ‘write’ access
 *
 * http://strava.github.io/api/v3/oauth/
 */
public class StravaScopes {
    public static final String SCOPE_PUBLIC = "public";
    public static final String SCOPE_VIEW_PRIVATE = "view_private";
    public static final String SCOPE_WRITE = "write";
    public static final String SCOPE_VIEW_PRIVATE_AND_WRITE = "view_private,write";
}
