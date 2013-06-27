
package com.wuman.oauth.samples.plurk.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import java.io.IOException;

public class Plurk extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "http://www.plurk.com/";

    public static final String DEFAULT_SERVICE_PATH = "APP/";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public Plurk(com.google.api.client.http.HttpTransport transport,
            com.google.api.client.json.JsonFactory jsonFactory,
            com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Plurk(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Timeline timeline() {
        return new Timeline();
    }

    public class Timeline {

        public GetPlurksRequest getPlurks() throws IOException {
            GetPlurksRequest request = new GetPlurksRequest();
            initialize(request);
            return request;
        }

        public class GetPlurksRequest extends
                PlurkRequest<com.wuman.oauth.samples.plurk.api.model.Timeline> {

            private static final String REST_PATH = "Timeline/getPlurks";

            @Key("offset")
            private String offsetRaw;

            private DateTime offset;

            @Key("limit")
            private String limit;

            @Key("minimal_data")
            private Integer minimalData;

            protected GetPlurksRequest() {
                super(Plurk.this,
                        HttpMethods.GET,
                        REST_PATH,
                        null,
                        com.wuman.oauth.samples.plurk.api.model.Timeline.class);
            }

            public final Boolean getMinimalData() {
                return this.minimalData == 1;
            }

            public final GetPlurksRequest setMinimalData(boolean minimalData) {
                this.minimalData = (minimalData ? 1 : 0);
                return this;
            }

            public final DateTime getOffset() {
                return offset;
            }

            public final GetPlurksRequest setOffset(DateTime offset) {
                this.offset = offset;
                this.offsetRaw = offset.toStringRfc3339().substring(0, 19);
                return this;
            }

            public final String getLimit() {
                return limit;
            }

            public final GetPlurksRequest setLimit(String limit) {
                this.limit = limit;
                return this;
            }

            @Override
            public GetPlurksRequest set(String fieldName, Object value) {
                return (GetPlurksRequest) super.set(fieldName, value);
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
        public Plurk build() {
            return new Plurk(this);
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

        public Builder setPlurkRequestInitializer(
                PlurkRequestInitializer instagramRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(instagramRequestInitializer);
        }

    }
}
