
package com.wuman.oauth.samples.github.api.model;

public abstract class GitHubResponse extends ClientErrors {

    public static final String KEY_DATA = "data";

    private Pagination pagination;

    public final Pagination getPagination() {
        return pagination;
    }

    public final GitHubResponse setPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    @Override
    public GitHubResponse clone() {
        return (GitHubResponse) super.clone();
    }

    @Override
    public GitHubResponse set(String fieldName, Object value) {
        return (GitHubResponse) super.set(fieldName, value);
    }

}
