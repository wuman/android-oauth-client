
package com.wuman.oauth.samples.github.api;

import android.text.TextUtils;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Key;
import com.google.api.client.util.ObjectParser;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.github.api.model.GitHubResponse;
import com.wuman.oauth.samples.github.api.model.Pagination;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Logger;

public class GitHubRequest<T> extends AbstractGoogleJsonClientRequest<T> {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    public GitHubRequest(GitHub client, String requestMethod,
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

    public final GitHubRequest<T> setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    @Override
    public GitHub getAbstractGoogleClient() {
        return (GitHub) super.getAbstractGoogleClient();
    }

    @Override
    public GitHubRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        return (GitHubRequest<T>) super.setDisableGZipContent(disableGZipContent);
    }

    @Override
    public GitHubRequest<T> setRequestHeaders(HttpHeaders headers) {
        return (GitHubRequest<T>) super.setRequestHeaders(headers);
    }

    @Override
    public GitHubRequest<T> set(String fieldName, Object value) {
        return (GitHubRequest<T>) super.set(fieldName, value);
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
            content = TextUtils.concat("{\"", GitHubResponse.KEY_DATA, "\":", content, "}")
                    .toString();
        }
        Reader reader = new StringReader(content);
        T parsedResponse = parser.parseAndClose(reader, getResponseClass());

        // parse pagination from Link header
        if (parsedResponse instanceof GitHubResponse) {
            Pagination pagination =
                    new Pagination(response.getHeaders().getFirstHeaderStringValue("Link"));
            ((GitHubResponse) parsedResponse).setPagination(pagination);
        }

        return parsedResponse;
    }

}
