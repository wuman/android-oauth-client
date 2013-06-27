
package com.wuman.oauth.samples.plurk.api;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

import java.io.IOException;

public class PlurkRequestInitializer extends CommonGoogleJsonClientRequestInitializer {

    public PlurkRequestInitializer() {
        super();
    }

    @Override
    protected void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
            throws IOException {
        super.initializeJsonRequest(request);
        initializePlurkRequest((PlurkRequest<?>) request);
    }

    protected void initializePlurkRequest(PlurkRequest<?> request)
            throws java.io.IOException {
    }

}
