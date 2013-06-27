
package com.wuman.oauth.samples.github.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Key;
import com.wuman.oauth.samples.github.api.model.Repositories;

import java.io.IOException;

public class GitHub extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "https://api.github.com/";

    public static final String DEFAULT_SERVICE_PATH = "";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public GitHub(com.google.api.client.http.HttpTransport transport,
            com.google.api.client.json.JsonFactory jsonFactory,
            com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    GitHub(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public User user() {
        return new User();
    }

    public class User {

        public ListReposRequest repos() throws IOException {
            ListReposRequest request = new ListReposRequest();
            initialize(request);
            return request;
        }

        public class ListReposRequest extends GitHubRequest<Repositories> {

            private static final String REST_PATH = "user/repos";

            public static final String TYPE_ALL = "all";
            public static final String TYPE_OWNER = "owner";
            public static final String TYPE_PUBLIC = "public";
            public static final String TYPE_PRIVATE = "private";
            public static final String TYPE_MEMBER = "member";

            public static final String SORT_CREATED = "created";
            public static final String SORT_UPDATED = "updated";
            public static final String SORT_PUSHED = "pushed";
            public static final String SORT_FULLNAME = "full_name";

            public static final String DIRECTION_ASC = "asc";
            public static final String DIRECTION_DESC = "desc";

            @Key("type")
            private String repoType;

            @Key("sort")
            private String sort;

            @Key("direction")
            private String direction;

            @Key("page")
            private Integer page;

            protected ListReposRequest() {
                super(GitHub.this,
                        HttpMethods.GET,
                        REST_PATH,
                        null,
                        Repositories.class);
            }

            public final String getRepoType() {
                return repoType;
            }

            public final ListReposRequest setRepoType(String repoType) {
                this.repoType = repoType;
                return this;
            }

            public final String getSort() {
                return sort;
            }

            public final ListReposRequest setSort(String sort) {
                this.sort = sort;
                return this;
            }

            public final String getDirection() {
                return direction;
            }

            public final ListReposRequest setDirection(String direction) {
                this.direction = direction;
                return this;
            }

            public final Integer getPage() {
                return page;
            }

            public final ListReposRequest setPage(Integer page) {
                this.page = page;
                return this;
            }

            @Override
            public ListReposRequest set(String fieldName, Object value) {
                return (ListReposRequest) super.set(fieldName, value);
            }

        }

    }

    public static final class Builder extends
            com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

        public Builder(com.google.api.client.http.HttpTransport transport,
                com.google.api.client.json.JsonFactory jsonFactory,
                com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
            super(transport,
                    jsonFactory,
                    DEFAULT_ROOT_URL,
                    DEFAULT_SERVICE_PATH,
                    httpRequestInitializer,
                    false);
        }

        @Override
        public GitHub build() {
            return new GitHub(this);
        }

        @Override
        public Builder setRootUrl(String rootUrl) {
            return (Builder) super.setRootUrl(rootUrl);
        }

        @Override
        public Builder setServicePath(String servicePath) {
            return (Builder) super.setServicePath(servicePath);
        }

        @Override
        public Builder setGoogleClientRequestInitializer(
                GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder) super
                    .setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }

        @Override
        public Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
            return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
        }

        @Override
        public Builder setApplicationName(String applicationName) {
            return (Builder) super.setApplicationName(applicationName);
        }

        @Override
        public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
            return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
        }

        @Override
        public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
            return (Builder) super
                    .setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
        }

        @Override
        public Builder setSuppressAllChecks(boolean suppressAllChecks) {
            return (Builder) super.setSuppressAllChecks(suppressAllChecks);
        }

        public Builder setGitHubRequestInitializer(
                GitHubRequestInitializer githubRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(githubRequestInitializer);
        }

    }
}
