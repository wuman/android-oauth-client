Android OAuth Client Library (android-oauth-client)
===================================================

![Feature Image](https://raw.github.com/wuman/android-oauth-client/master/library/src/site/static/feature.png)

![Screenshot](https://raw.github.com/wuman/android-oauth-client/master/library/src/site/static/screenshot.png)

The `android-oauth-client` library helps you to easily add an OAuth flow to
your existing Android application.  It automatically shows a customizable
Android dialog with `WebView` to guide the user to eventually grant you an
access token.

To help you manage access tokens, the library also includes an out-of-the-box
credential store which stores tokens in
[SharedPreferences](http://d.android.com/reference/android/content/SharedPreferences.html).

This client library is an Android extension to the
[Google OAuth Client Library for Java](https://code.google.com/p/google-oauth-java-client/).


FEATURES
--------

* Supports the following OAuth flows via the `google-oauth-java-client` library:
    - [OAuth 1.0a](http://oauth.net/core/1.0a/)
    - OAuth 2.0 [Explicit Authorization](http://tools.ietf.org/html/rfc6749#section-1.3.1) (authorization code)
    - OAuth 2.0 [Implicit Authorization](http://tools.ietf.org/html/rfc6749#section-1.3.2)
* Automatically handles the OAuth flow in both `android.app.DialogFragment` and
  `android.support.v4.app.DialogFragment`.  The dialog UI is fully customizable.
* Provides an out-of-the-box credential store implementation via `SharedPreferences`.
* Provides sample applications that already work with popular social network
  services such as:
    - [Flickr](http://www.flickr.com/services/api/auth.oauth.html) (OAuth 1.0a)
    - [Foursquare](https://developer.foursquare.com/overview/auth) (OAuth 2.0 Explicit/Implicit)
    - [GitHub](http://developer.github.com/v3/oauth/) (OAuth 2.0 Explicit)
    - [Instagram](http://instagram.com/developer/authentication/) (OAuth 2.0 Implicit)
    - [LinkedIn](http://developer.linkedin.com/documents/authentication) (OAuth 1.0a, OAuth 2.0 Explicit)
    - [Plurk](http://www.plurk.com/API#oauth_flow) (OAuth 1.0a)
    - [Twitter](https://dev.twitter.com/docs/auth/3-legged-authorization) (OAuth 1.0a)


**Note on OAuth flows:** In general you should prefer OAuth 2.0 Implicit
Authorization over OAuth 2.0 Explicit Authorization because implicit
authorization does not require the client secret to be stored in the client.


USAGE
-----

Regardless of which OAuth flow you intend to incorporate into your Android
application, the `android-oauth-client` library can be used in 2 simple steps:

1. Obtain an instance of `OAuthManager` by supplying the following 2 parameters:
    - An `AuthorizationFlow` instance which automatically handles the OAuth flow logic,
    - An `AuthorizationUIController` which manages the UI.
2. Call one of the 3 possible `authorize` methods on `OAuthManager`.  The call
   may be called from any thread either synchronously or asynchronously with
   an `OAuthCallback<Credential>`.
    - `OAuthManager.authorize10a()`
    - `OAuthManager.authorizeExplicitly()`
    - `OAuthManager.authorizeImplicitly()`

We will go into more detail about each of the steps.


### Obtaining an OAuthManager instance ###

`OAuthManager` can be obtained by direct instantiation:

    OAuthManager oauth = new OAuthManager(flow, controller);


### Obtaining an access token via the OAuthManager ###

To start the OAuth flow and obtain an access token, call one of the `authorize()`
methods according to the authorization flow of your choice.

You may invoke the `authorize()` method *synchronously*:

    Credential credential = oauth.authorizeImplicitly("userId", null, null).getResult();
    // continue to make API queries with credential.getAccessToken()

You may also invoke the `authorize()` method *asynchronously* with an `OAuthCallback`,
executed on a `android.os.Handler` of your choice.

    OAuthCallback<Credential> callback = new OAuthCallback<Credential>() {
        @Override public void run(OAuthFuture<Credential> future) {
            Credential credential = future.getResult();
            // make API queries with credential.getAccessToken()
        }
    };
    oauth.authorizeImplicitly("userId", callback, handler);

Note that if a `Handler` is not supplied, the `callback` will be invoked on the
main thread.


### CredentialStore ###

Use the provided `SharedPreferencesCredentialStore`, which automatically
serializes access tokens to and from `SharedPreferences` in JSON format.

    SharedPreferencesCredentialStore credentialStore =
        new SharedPreferencesCredentialStore(context,
            "preferenceFileName", new JacksonFactory());


### AuthorizationFlow ###

An `AuthorizationFlow` instance may be obtained via its `Builder`:

    AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
        BearerToken.authorizationHeaderAccessMethod(),
        AndroidHttp.newCompatibleTransport(),
        new JacksonFactory(),
        new GenericUrl("https://socialservice.com/oauth2/access_token"),
        new ClientParametersAuthentication("CLIENT_ID", "CLIENT_SECRET"),
        "CLIENT_ID",
        "https://socialservice.com/oauth2/authorize");
    builder.setCredentialStore(credentialStore);
    AuthorizationFlow flow = builder.build();

For OAuth 2.0 flows, you may wish to add OAuth scopes:

    builder.setScopes(Arrays.asList("scope1", "scope2"));

For the OAuth 1.0a flow, you need to set the temporary token request URL:

    builder.setTemporaryTokenRequestUrl("https://socialservice.com/oauth/requestToken");

Note that `CLIENT_SECRET` may be omitted and be replaced with a `null` value
for the OAuth 2.0 Implicit Authorization flow.

Also, `CLIENT_ID` and `CLIENT_SECRET` are called `CONSUMER_KEY` and `CONSUMER_SECRET`
in the OAuth 1.0a flow.


### AuthorizationUIController ###

Use the provided `DialogFragmentController`, which automatically handles most
of the UI for you via an Android dialog.  The `DialogFragmentController` has
two constructors, one taking `android.app.FragmentManager` and the other taking
`android.support.v4.app.FragmentManager` as the sole input parameter.  Depending
on how you instantiate the controller, either `android.app.DialogFragment` or
`android.support.v4.app.DialogFragment` will be used.

    AuthorizationUIController controller =
        new DialogFragmentController(getFragmentManager()) {

            @Override
            public String getRedirectUri() throws IOException {
                return "http://localhost/Callback";
            }

            @Override
            public boolean isJavascriptEnabledForWebView() {
                return true;
            }

        };


PROGUARD
--------

On Android it is typical to use Proguard to obfuscate and shrink code in order
to reduce application size and improve security.  If you are using Proguard,
make sure you include the following configurations:

    # Needed to keep generic types and @Key annotations accessed via reflection
    -keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

    -keepclasseswithmembers class * {
      @com.google.api.client.util.Key <fields>;
    }

    -keepclasseswithmembers class * {
      @com.google.api.client.util.Value <fields>;
    }

    -keepnames class com.google.api.client.http.HttpTransport

    # Needed by google-http-client-android when linking against an older platform version
    -dontwarn com.google.api.client.extensions.android.**

    # Needed by google-api-client-android when linking against an older platform version
    -dontwarn com.google.api.client.googleapis.extensions.android.**

    # Do not obfuscate but allow shrinking of android-oauth-client
    -keepnames class com.wuman.android.auth.** { *; }


SAMPLES
-------

A sample application is provided to showcase how you might use the library in
a real-life Android application.  In addition to default usage, the sample
application also has examples for customizing the Dialog UI as well as OAuth
integrations for popular social network services such as Flickr, LinkedIn,
Twitter, etc.

HOW TO USE THE LIBRARY
----------------------

Android OAuth Client is available for download from the [Maven Central](http://bit.ly/15XDPPe)
repository.  You may also include it as a dependency by including the following
coordinates:

```xml
<dependency>
  <groupId>com.wu-man</groupId>
  <artifactId>android-oauth-client</artifactId>
  <version>0.4.5</version>
</dependency>
```

or using Gradle

```groovy
dependencies {
   compile 'com.wu-man:android-oauth-client:0.4.5'
}
```

As of v0.0.3, an Android library `AAR` format is available as well.
The artifact coordinates are:

```xml
<dependency>
  <groupId>com.wu-man</groupId>
  <artifactId>android-oauth-client</artifactId>
  <version>0.4.5</version>
  <type>aar</type>
</dependency>
```


HOW TO BUILD THE PROJECT
------------------------

You should use the included Gradle wrapper to build the project with the
following command:

    ./gradlew clean build

The resulting jar file is located at `library/build/libs/library.jar` and the
aar file is located at `library/build/libs/library.aar`.


CONTRIBUTE
----------

If you would like to contribute code to android-oauth-client you can do so through
GitHub by forking the repository and sending a pull request.

In general if you are contributing we ask you to follow the
[AOSP coding style guidelines](http://source.android.com/source/code-style.html).
If you are using an IDE such as Eclipse, it is easiest to use the
[AOSP style formatters](http://source.android.com/source/using-eclipse.html#eclipse-formatting).

You may [file an issue](https://github.com/wuman/android-oauth-client/issues/new)
if you find bugs or would like to add a new feature.

We do not have a mailing list.  All questions should be asked and will be answered
on [StackOverflow using the android-oauth-client tag](http://stackoverflow.com/questions/tagged/android-oauth-client).


DEVELOPED BY
------------

* David Wu - <david@wu-man.com> - [http://blog.wu-man.com](http://blog.wu-man.com)


LICENSE
-------

    Copyright 2013 David Wu
    Copyright (C) 2010 Google Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/wuman/android-oauth-client/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
