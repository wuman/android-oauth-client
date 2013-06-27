
package com.wuman.oauth.samples.instagram.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Key;

import java.io.IOException;

public class Instagram extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "https://api.instagram.com/";

    public static final String DEFAULT_SERVICE_PATH = "v1/";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public Instagram(com.google.api.client.http.HttpTransport transport,
            com.google.api.client.json.JsonFactory jsonFactory,
            com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Instagram(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Users users() {
        return new Users();
    }

    public class Users {

        public Self self() {
            return new Self();
        }

        public class Self {

            public FeedRequest feed() throws IOException {
                FeedRequest result = new FeedRequest();
                initialize(result);
                return result;
            }

            public class FeedRequest
                    extends
                    InstagramRequest<com.wuman.oauth.samples.instagram.api.model.Feed> {

                private static final String REST_PATH = "users/self/feed";

                @Key("count")
                private Integer count;

                @Key("min_id")
                private String minId;

                @Key("max_id")
                private String maxId;

                protected FeedRequest() {
                    super(Instagram.this,
                            HttpMethods.GET,
                            REST_PATH,
                            null,
                            com.wuman.oauth.samples.instagram.api.model.Feed.class);
                }

                public final FeedRequest setCount(Integer count) {
                    this.count = count;
                    return this;
                }

                public final FeedRequest setMinId(String minId) {
                    this.minId = minId;
                    return this;
                }

                public final FeedRequest setMaxId(String maxId) {
                    this.maxId = maxId;
                    return this;
                }

                @Override
                public FeedRequest setAccessToken(String accessToken) {
                    return (FeedRequest) super.setAccessToken(accessToken);
                }

                @Override
                public FeedRequest set(String fieldName, Object value) {
                    return (FeedRequest) super.set(fieldName, value);
                }

                @Override
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

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
        public Instagram build() {
            return new Instagram(this);
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

        public Builder setInstagramRequestInitializer(
                InstagramRequestInitializer instagramRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(instagramRequestInitializer);
        }

    }
}
