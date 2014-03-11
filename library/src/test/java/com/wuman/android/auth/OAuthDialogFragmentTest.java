
package com.wuman.android.auth;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OAuthDialogFragmentTest extends TestCase {

    @Test
    public void testRedirectUriDetection() {
        String r1 = "http://localhost";
        String r2 = "http://localhost:8080";
        String r3 = "http://localhost:8080/path";
        String r4 = "http://localhost:8080/path?a=1&b=2";
        String r5 = "http://localhost:8080/path#frag";

        assertTrue(OAuthDialogFragment.isRedirectUriFound(r1, r1));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r2, r2));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r3, r2));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r4, r2));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r5, r2));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r4, r3));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r5, r3));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r4, r4));
        assertTrue(OAuthDialogFragment.isRedirectUriFound(r5, r5));

        assertFalse(OAuthDialogFragment.isRedirectUriFound(r1, r2));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r1, r3));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r1, r4));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r1, r5));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r2, r1));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r2, r3));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r2, r4));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r2, r5));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r3, r1));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r3, r4));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r3, r5));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r4, r1));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r4, r5));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r5, r1));
        assertFalse(OAuthDialogFragment.isRedirectUriFound(r5, r4));

        assertTrue(OAuthDialogFragment.isRedirectUriFound(r4 + "&c=3", r4));
        assertTrue(OAuthDialogFragment.isRedirectUriFound("http://localhost:8080/path?b=2&a=1", r4));
        assertTrue(OAuthDialogFragment.isRedirectUriFound("http://localhost:8080/path?c=3&b=2&a=1", r4));
    }

}
