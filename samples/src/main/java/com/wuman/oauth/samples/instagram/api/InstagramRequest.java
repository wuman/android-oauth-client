
package com.wuman.oauth.samples.instagram.api;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.Key;

public class InstagramRequest<T> extends AbstractGoogleJsonClientRequest<T> {

    public InstagramRequest(Instagram client, String requestMethod,
            String uriTemplate, Object content, Class<T> responseClass) {
        super(client,
                requestMethod,
                uriTemplate,
                content,
                responseClass);
    }

    @Key("access_token")
    private String accessToken;

    public final String getAccessToken() {
        return accessToken;
    }

    public InstagramRequest<T> setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    @Override
    public Instagram getAbstractGoogleClient() {
        return (Instagram) super.getAbstractGoogleClient();
    }

    @Override
    public InstagramRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        return (InstagramRequest<T>) super.setDisableGZipContent(disableGZipContent);
    }

    @Override
    public InstagramRequest<T> setRequestHeaders(HttpHeaders headers) {
        return (InstagramRequest<T>) super.setRequestHeaders(headers);
    }

    @Override
    public InstagramRequest<T> set(String fieldName, Object value) {
        return (InstagramRequest<T>) super.set(fieldName, value);
    }

}
