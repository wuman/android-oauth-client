
package com.wuman.oauth.samples.twitter.api;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

import java.io.IOException;

public class TwitterRequestInitializer extends CommonGoogleJsonClientRequestInitializer {

    public TwitterRequestInitializer() {
        super();
    }

    @Override
    protected void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
            throws IOException {
        super.initializeJsonRequest(request);
        initializeTwitterRequest((TwitterRequest<?>) request);
    }

    protected void initializeTwitterRequest(TwitterRequest<?> request)
            throws java.io.IOException {
    }

}
