/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wuman.android.auth.oauth;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Clock;
import com.google.api.client.util.Preconditions;

import java.io.IOException;
import java.util.Collection;

/**
 * OAuth 1.0a {@link Credential}. Most of the implementation is identical to
 * that of
 * {@link com.google.api.client.extensions.oauth.helpers.oauth.OAuthHmacCredential}
 * in <a href="https://code.google.com/p/google-oauth-java-client/">Google OAuth
 * Client Library for Java</a>.
 * 
 * @author David Wu
 */
public class OAuthHmacCredential extends Credential {

    private String consumerKey;
    private String sharedSecret;
    private String tokenSharedSecret;

    private OAuthParameters authorizer;

    protected OAuthHmacCredential(Builder builder) {
        super(builder);
        this.consumerKey = builder.consumerKey;
        this.sharedSecret = builder.sharedSecret;
        postConstruct();
    }

    private void postConstruct() {
        OAuthHmacSigner signer = new OAuthHmacSigner();
        signer.clientSharedSecret = sharedSecret;
        signer.tokenSharedSecret = tokenSharedSecret;

        authorizer = new OAuthParameters();
        authorizer.consumerKey = consumerKey;
        authorizer.signer = signer;
        authorizer.token = getAccessToken();
    }

    public final String getConsumerKey() {
        return consumerKey;
    }

    public OAuthHmacCredential setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
        postConstruct();
        return this;
    }

    public final String getSharedSecret() {
        return sharedSecret;
    }

    public OAuthHmacCredential setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
        postConstruct();
        return this;
    }

    public final String getTokenSharedSecret() {
        return tokenSharedSecret;
    }

    public OAuthHmacCredential setTokenSharedSecret(String tokenSharedSecret) {
        this.tokenSharedSecret = tokenSharedSecret;
        postConstruct();
        return this;
    }

    @Override
    public OAuthHmacCredential setAccessToken(String accessToken) {
        super.setAccessToken(accessToken);
        postConstruct();
        return this;
    }

    @Override
    public OAuthHmacCredential setRefreshToken(String refreshToken) {
        return (OAuthHmacCredential) super.setRefreshToken(refreshToken);
    }

    @Override
    public OAuthHmacCredential setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
        return (OAuthHmacCredential) super
                .setExpirationTimeMilliseconds(expirationTimeMilliseconds);
    }

    @Override
    public OAuthHmacCredential setExpiresInSeconds(Long expiresIn) {
        return (OAuthHmacCredential) super.setExpiresInSeconds(expiresIn);
    }

    @Override
    public OAuthHmacCredential setFromTokenResponse(TokenResponse tokenResponse) {
        return (OAuthHmacCredential) super.setFromTokenResponse(tokenResponse);
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        authorizer.initialize(request);
        super.initialize(request);
    }

    @Override
    public void intercept(HttpRequest request) throws IOException {
        super.intercept(request);
        authorizer.intercept(request);
    }

    @Override
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) {
        if (response.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED) {
            // If the token was revoked, we must mark our credential as invalid
            setAccessToken(null);
        }

        // We didn't do anything to fix the problem
        return false;
    }

    public static class Builder extends Credential.Builder {

        String consumerKey;
        String sharedSecret;

        public Builder(AccessMethod method, String consumerKey, String sharedSecret) {
            super(method);
            this.consumerKey = Preconditions.checkNotNull(consumerKey);
            this.sharedSecret = Preconditions.checkNotNull(sharedSecret);
        }

        /** Returns a new credential instance. */
        public OAuthHmacCredential build() {
            return new OAuthHmacCredential(this);
        }

        public final String getConsumerKey() {
            return consumerKey;
        }

        public Builder setConsumerKey(String consumerKey) {
            this.consumerKey = consumerKey;
            return this;
        }

        public final String getSharedSecret() {
            return sharedSecret;
        }

        public Builder setSharedSecret(String sharedSecret) {
            this.sharedSecret = sharedSecret;
            return this;
        }

        @Override
        public Builder setTransport(HttpTransport transport) {
            return (Builder) super.setTransport(transport);
        }

        @Override
        public Builder setClock(Clock clock) {
            return (Builder) super.setClock(clock);
        }

        @Override
        public Builder setJsonFactory(JsonFactory jsonFactory) {
            return (Builder) super.setJsonFactory(jsonFactory);
        }

        @Override
        public Builder setTokenServerUrl(GenericUrl tokenServerUrl) {
            return (Builder) super.setTokenServerUrl(tokenServerUrl);
        }

        @Override
        public Builder setTokenServerEncodedUrl(String tokenServerEncodedUrl) {
            return (Builder) super.setTokenServerEncodedUrl(tokenServerEncodedUrl);
        }

        @Override
        public Builder setClientAuthentication(HttpExecuteInterceptor clientAuthentication) {
            return (Builder) super.setClientAuthentication(clientAuthentication);
        }

        @Override
        public Builder setRequestInitializer(HttpRequestInitializer requestInitializer) {
            return (Builder) super.setRequestInitializer(requestInitializer);
        }

        @Override
        public Builder setRefreshListeners(Collection<CredentialRefreshListener> refreshListeners) {
            return (Builder) super.setRefreshListeners(refreshListeners);
        }

        @Override
        public Builder addRefreshListener(CredentialRefreshListener refreshListener) {
            return (Builder) super.addRefreshListener(refreshListener);
        }

    }

}
