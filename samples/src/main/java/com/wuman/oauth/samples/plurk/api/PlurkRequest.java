
package com.wuman.oauth.samples.plurk.api;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;

public class PlurkRequest<T> extends AbstractGoogleJsonClientRequest<T> {

    public PlurkRequest(Plurk client, String requestMethod,
            String uriTemplate, Object content, Class<T> responseClass) {
        super(client,
                requestMethod,
                uriTemplate,
                content,
                responseClass);
    }

    @Override
    public Plurk getAbstractGoogleClient() {
        return (Plurk) super.getAbstractGoogleClient();
    }

    @Override
    public PlurkRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        return (PlurkRequest<T>) super.setDisableGZipContent(disableGZipContent);
    }

    @Override
    public PlurkRequest<T> setRequestHeaders(HttpHeaders headers) {
        return (PlurkRequest<T>) super.setRequestHeaders(headers);
    }

    @Override
    public PlurkRequest<T> set(String fieldName, Object value) {
        return (PlurkRequest<T>) super.set(fieldName, value);
    }

}
