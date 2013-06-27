
package com.wuman.oauth.samples.flickr.api;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

import java.io.IOException;

public class FlickrRequestInitializer extends CommonGoogleJsonClientRequestInitializer {

    public FlickrRequestInitializer() {
        super();
    }

    @Override
    protected void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
            throws IOException {
        super.initializeJsonRequest(request);
        initializeFlickrRequest((FlickrRequest<?>) request);
    }

    protected void initializeFlickrRequest(FlickrRequest<?> request)
            throws java.io.IOException {
        request.setFormat("json");
        request.setNoJsonCallback(true);
    }

}
