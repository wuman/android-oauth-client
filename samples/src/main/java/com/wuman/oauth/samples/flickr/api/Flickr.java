
package com.wuman.oauth.samples.flickr.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Key;

import java.io.IOException;

public class Flickr extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "https://secure.flickr.com/";

    public static final String DEFAULT_SERVICE_PATH = "services/rest/";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public Flickr(com.google.api.client.http.HttpTransport transport,
            com.google.api.client.json.JsonFactory jsonFactory,
            com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Flickr(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Photos photos() {
        return new Photos();
    }

    public class Photos {

        public GetContactsPhotos getContactsPhotos() throws IOException {
            GetContactsPhotos request = new GetContactsPhotos();
            initialize(request);
            return request;
        }

        public class GetContactsPhotos extends
                FlickrRequest<com.wuman.oauth.samples.flickr.api.model.ContactsPhotos> {

            private static final String METHOD_NAME = "flickr.photos.getContactsPhotos";

            @Key("count")
            private Integer count;

            @Key("just_friends")
            private Integer justFriends;

            @Key("single_photo")
            private Integer singlePhoto;

            @Key("include_self")
            private Integer includeSelf;

            @Key("extras")
            private String extras;

            protected GetContactsPhotos() {
                super(Flickr.this,
                        HttpMethods.GET,
                        "",
                        null,
                        com.wuman.oauth.samples.flickr.api.model.ContactsPhotos.class);
                setMethod(METHOD_NAME);
            }

            public final Integer getCount() {
                return count;
            }

            public final GetContactsPhotos setCount(Integer count) {
                this.count = count;
                return this;
            }

            public final Boolean getJustFriends() {
                return justFriends != 0;
            }

            public final GetContactsPhotos setJustFriends(Boolean justFriends) {
                this.justFriends = justFriends ? 1 : 0;
                return this;
            }

            public final Boolean getSinglePhoto() {
                return singlePhoto != 0;
            }

            public final GetContactsPhotos setSinglePhoto(Boolean singlePhoto) {
                this.singlePhoto = singlePhoto ? 1 : 0;
                return this;
            }

            public final Boolean getIncludeSelf() {
                return includeSelf != 0;
            }

            public final GetContactsPhotos setIncludeSelf(Boolean includeSelf) {
                this.includeSelf = includeSelf ? 1 : 0;
                return this;
            }

            public final String getExtras() {
                return extras;
            }

            public final GetContactsPhotos setExtras(String extras) {
                this.extras = extras;
                return this;
            }

            @Override
            public GetContactsPhotos set(String fieldName, Object value) {
                return (GetContactsPhotos) super.set(fieldName, value);
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
        public Flickr build() {
            return new Flickr(this);
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

        public Builder setFlickrRequestInitializer(
                FlickrRequestInitializer instagramRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(instagramRequestInitializer);
        }

    }
}
