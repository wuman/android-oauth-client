
package com.wuman.oauth.samples.github.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

public class GitHubRequestInitializer extends CommonGoogleJsonClientRequestInitializer {

    private final Credential credential;

    public GitHubRequestInitializer(Credential credential) {
        super();
        this.credential = Preconditions.checkNotNull(credential);
    }

    @Override
    protected void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
            throws IOException {
        super.initializeJsonRequest(request);
        initializeGitHubRequest((GitHubRequest<?>) request);
    }

    protected void initializeGitHubRequest(GitHubRequest<?> request)
            throws java.io.IOException {
        request.setAccessToken(credential.getAccessToken());
    }

}
