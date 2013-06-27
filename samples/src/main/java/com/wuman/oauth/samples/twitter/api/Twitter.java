
package com.wuman.oauth.samples.twitter.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Key;
import com.wuman.oauth.samples.twitter.api.model.Timeline;

import java.io.IOException;

public class Twitter extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "https://api.twitter.com/";

    public static final String DEFAULT_SERVICE_PATH = "1.1/";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public Twitter(com.google.api.client.http.HttpTransport transport,
            com.google.api.client.json.JsonFactory jsonFactory,
            com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Twitter(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Statuses statuses() {
        return new Statuses();
    }

    public class Statuses {

        public HomeTimelineRequest homeTimelines() throws IOException {
            HomeTimelineRequest request = new HomeTimelineRequest();
            initialize(request);
            return request;
        }

        public class HomeTimelineRequest extends TwitterRequest<Timeline> {

            private static final String REST_PATH = "statuses/home_timeline.json";

            @Key("count")
            private Integer count;

            @Key("max_id")
            private String maxId;

            protected HomeTimelineRequest() {
                super(Twitter.this,
                        HttpMethods.GET,
                        REST_PATH,
                        null,
                        Timeline.class);
            }

            public final Integer getCount() {
                return count;
            }

            public final HomeTimelineRequest setCount(Integer count) {
                this.count = count;
                return this;
            }

            public final String getMaxId() {
                return maxId;
            }

            public final HomeTimelineRequest setMaxId(String maxId) {
                this.maxId = maxId;
                return this;
            }

            @Override
            public HomeTimelineRequest set(String fieldName, Object value) {
                return (HomeTimelineRequest) super.set(fieldName, value);
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
        public Twitter build() {
            return new Twitter(this);
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

        public Builder setTwitterRequestInitializer(
                TwitterRequestInitializer instagramRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(instagramRequestInitializer);
        }

    }
}
