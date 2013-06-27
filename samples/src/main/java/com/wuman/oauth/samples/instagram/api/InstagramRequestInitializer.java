
package com.wuman.oauth.samples.instagram.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

public class InstagramRequestInitializer extends CommonGoogleJsonClientRequestInitializer {

    private final Credential credential;

    public InstagramRequestInitializer(Credential credential) {
        super();
        this.credential = Preconditions.checkNotNull(credential);
    }

    @Override
    protected void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
            throws IOException {
        super.initializeJsonRequest(request);
        initializeInstagramRequest((InstagramRequest<?>) request);
    }

    protected void initializeInstagramRequest(InstagramRequest<?> request)
            throws java.io.IOException {
        request.setAccessToken(credential.getAccessToken());
    }

}
