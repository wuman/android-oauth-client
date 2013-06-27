
package com.wuman.oauth.samples.twitter.api;

import android.text.TextUtils;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.ObjectParser;
import com.wuman.oauth.samples.twitter.api.model.TwitterResponse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TwitterRequest<T> extends AbstractGoogleJsonClientRequest<T> {

    public TwitterRequest(Twitter client, String requestMethod,
            String uriTemplate, Object content, Class<T> responseClass) {
        super(client,
                requestMethod,
                uriTemplate,
                content,
                responseClass);
    }

    @Override
    public Twitter getAbstractGoogleClient() {
        return (Twitter) super.getAbstractGoogleClient();
    }

    @Override
    public TwitterRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        return (TwitterRequest<T>) super.setDisableGZipContent(disableGZipContent);
    }

    @Override
    public TwitterRequest<T> setRequestHeaders(HttpHeaders headers) {
        return (TwitterRequest<T>) super.setRequestHeaders(headers);
    }

    @Override
    public TwitterRequest<T> set(String fieldName, Object value) {
        return (TwitterRequest<T>) super.set(fieldName, value);
    }

    @Override
    public T execute() throws IOException {
        HttpResponse response = super.executeUnparsed();
        ObjectParser parser = response.getRequest().getParser();
        // This will degrade parsing performance but is an inevitable workaround
        // for the inability to parse JSON arrays.
        String content = response.parseAsString();
        if (response.isSuccessStatusCode()
                && !TextUtils.isEmpty(content)
                && content.charAt(0) == '[') {
            content = TextUtils.concat("{\"", TwitterResponse.KEY_DATA, "\":", content, "}")
                    .toString();
        }
        Reader reader = new StringReader(content);
        return parser.parseAndClose(reader, getResponseClass());
    }

}
