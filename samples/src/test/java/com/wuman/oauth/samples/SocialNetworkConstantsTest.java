
package com.wuman.oauth.samples;

import android.text.TextUtils;

import com.wuman.oauth.samples.flickr.FlickrConstants;
import com.wuman.oauth.samples.foursquare.FoursquareConstants;
import com.wuman.oauth.samples.github.GitHubConstants;
import com.wuman.oauth.samples.instagram.InstagramConstants;
import com.wuman.oauth.samples.linkedin.LinkedInConstants;
import com.wuman.oauth.samples.plurk.PlurkConstants;
import com.wuman.oauth.samples.twitter.TwitterConstants;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SocialNetworkConstantsTest extends TestCase {

    @Test
    public void testFlickrConstants() {
        assertFalse(TextUtils.isEmpty(FlickrConstants.CONSUMER_KEY));
        assertFalse(TextUtils.isEmpty(FlickrConstants.CONSUMER_SECRET));
    }

    @Test
    public void testFoursquareConstants() {
        assertFalse(TextUtils.isEmpty(FoursquareConstants.CLIENT_ID));
        assertFalse(TextUtils.isEmpty(FoursquareConstants.CLIENT_SECRET));
    }

    @Test
    public void testGitHubConstants() {
        assertFalse(TextUtils.isEmpty(GitHubConstants.CLIENT_ID));
        assertFalse(TextUtils.isEmpty(GitHubConstants.CLIENT_SECRET));
    }

    @Test
    public void testInstagramConstants() {
        assertFalse(TextUtils.isEmpty(InstagramConstants.CLIENT_ID));
    }

    @Test
    public void testLinkedInConstants() {
        assertFalse(TextUtils.isEmpty(LinkedInConstants.CLIENT_ID));
        assertFalse(TextUtils.isEmpty(LinkedInConstants.CLIENT_SECRET));
    }

    @Test
    public void testPlurkConstants() {
        assertFalse(TextUtils.isEmpty(PlurkConstants.CONSUMER_KEY));
        assertFalse(TextUtils.isEmpty(PlurkConstants.CONSUMER_SECRET));
    }

    @Test
    public void testTwitterConstants() {
        assertFalse(TextUtils.isEmpty(TwitterConstants.CONSUMER_KEY));
        assertFalse(TextUtils.isEmpty(TwitterConstants.CONSUMER_SECRET));
    }

}
