
package com.wuman.android.auth.oauth;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;

public class OAuth10aResponseUrl extends GenericUrl {

    /** Temporary access token issued by the authorization server. */
    @Key("oauth_token")
    private String token;

    /** Verifier code issued by the authorization server. */
    @Key("oauth_verifier")
    private String verifier;

    /** Error */
    @Key("error")
    private String error;

    OAuth10aResponseUrl() {
        super();
    }

    public OAuth10aResponseUrl(String encodedUrl) {
        super(encodedUrl);
    }

    /** Returns the temporary access token issued by the authorization server. */
    public final String getToken() {
        return token;
    }

    /**
     * Sets the temporary access token issued by the authorization server.
     * <p>
     * Overriding is only supported for the purpose of calling the super
     * implementation and changing the return type, but nothing else.
     * </p>
     */
    public OAuth10aResponseUrl setToken(String token) {
        this.token = Preconditions.checkNotNull(token);
        return this;
    }

    public final String getVerifier() {
        return verifier;
    }

    public OAuth10aResponseUrl setVerifier(String verifier) {
        this.verifier = Preconditions.checkNotNull(verifier);
        return this;
    }

    public final String getError() {
        return error;
    }

    public OAuth10aResponseUrl setError(String error) {
        this.error = error;
        return this;
    }

    @Override
    public OAuth10aResponseUrl set(String fieldName, Object value) {
        return (OAuth10aResponseUrl) super.set(fieldName, value);
    }

    @Override
    public OAuth10aResponseUrl clone() {
        return (OAuth10aResponseUrl) super.clone();
    }

}
