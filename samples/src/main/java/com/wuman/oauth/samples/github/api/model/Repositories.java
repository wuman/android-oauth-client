
package com.wuman.oauth.samples.github.api.model;

import com.google.api.client.util.Key;

import java.util.List;

public class Repositories extends GitHubResponse {

    @Key(KEY_DATA)
    private List<Repository> repositories;

    public final List<Repository> getRepositories() {
        return repositories;
    }

    @Override
    public Repositories clone() {
        return (Repositories) super.clone();
    }

    @Override
    public Repositories set(String fieldName, Object value) {
        return (Repositories) super.set(fieldName, value);
    }

}
