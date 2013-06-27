/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wuman.android.auth.oauth2.explicit;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.wuman.android.auth.OAuthConstants;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 * OAuth 2.0 request for an access token using an authorization code as
 * specified in <a
 * href="http://tools.ietf.org/html/rfc6749#section-4.1.3">Access Token
 * Request</a>.
 * <p>
 * Use {@link Credential} to access protected resources from the resource server
 * using the {@link TokenResponse} returned by {@link #execute()}. On error, it
 * will instead throw {@link TokenResponseException}.
 * </p>
 * <p>
 * Sample usage:
 * </p>
 * 
 * <pre>
 * static void requestAccessToken() throws IOException {
 *     try {
 *         TokenResponse response =
 *                 new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
 *                         new GenericUrl(&quot;https://server.example.com/token&quot;),
 *                         &quot;SplxlOBeZQQYbYS6WxSbIA&quot;)
 *                         .setRedirectUri(&quot;https://client.example.com/rd&quot;)
 *                         .setClientAuthentication(
 *                                 new BasicAuthentication(&quot;s6BhdRkqt3&quot;, &quot;7Fjfp0ZBr1KtDRbnfVdmIw&quot;))
 *                         .execute();
 *         System.out.println(&quot;Access token: &quot; + response.getAccessToken());
 *     } catch (TokenResponseException e) {
 *         if (e.getDetails() != null) {
 *             System.err.println(&quot;Error: &quot; + e.getDetails().getError());
 *             if (e.getDetails().getErrorDescription() != null) {
 *                 System.err.println(e.getDetails().getErrorDescription());
 *             }
 *             if (e.getDetails().getErrorUri() != null) {
 *                 System.err.println(e.getDetails().getErrorUri());
 *             }
 *         } else {
 *             System.err.println(e.getMessage());
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * Some OAuth 2.0 providers don't support {@link BasicAuthentication} but
 * instead support {@link ClientParametersAuthentication}. In the above sample
 * code, simply replace the class name and it will work the same way.
 * </p>
 * <p>
 * Implementation is not thread-safe.
 * </p>
 * 
 * @since 1.7
 * @author Yaniv Inbar
 * @author David Wu
 */
public class LenientAuthorizationCodeTokenRequest extends AuthorizationCodeTokenRequest {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    public LenientAuthorizationCodeTokenRequest(HttpTransport transport, JsonFactory jsonFactory,
            GenericUrl tokenServerUrl, String code) {
        super(transport, jsonFactory, tokenServerUrl, code);
    }

    @Override
    public TokenResponse execute() throws IOException {
        return executeLeniently();
    }

    /**
     * Executes request for an access token, and returns the HTTP response.
     * <p>
     * To execute and parse the response to {@link TokenResponse}, instead use
     * {@link #execute()}.
     * </p>
     * <p>
     * Callers should call {@link HttpResponse#disconnect} when the returned
     * HTTP response object is no longer needed. However,
     * {@link HttpResponse#disconnect} does not have to be called if the
     * response stream is properly closed. Example usage:
     * </p>
     * 
     * <pre>
     * HttpResponse response = tokenRequest.executeUnparsed();
     * try {
     *     // process the HTTP response object
     * } finally {
     *     response.disconnect();
     * }
     * </pre>
     * 
     * @return successful access token response, which can then be parsed
     *         directly using {@link HttpResponse#parseAs(Class)} or some other
     *         parsing method
     * @throws TokenResponseException for an error response
     */
    private TokenResponse executeLeniently() throws IOException {
        // must set clientAuthentication as last execute interceptor in case it
        // needs to sign request
        HttpRequestFactory requestFactory =
                getTransport().createRequestFactory(new HttpRequestInitializer() {

                    public void initialize(HttpRequest request) throws IOException {
                        if (getRequestInitializer() != null) {
                            getRequestInitializer().initialize(request);
                        }
                        final HttpExecuteInterceptor interceptor = request.getInterceptor();
                        request.setInterceptor(new HttpExecuteInterceptor() {
                            public void intercept(HttpRequest request) throws IOException {
                                if (interceptor != null) {
                                    interceptor.intercept(request);
                                }
                                if (getClientAuthentication() != null) {
                                    getClientAuthentication().intercept(request);
                                }
                            }
                        });
                    }
                });
        // make request
        HttpRequest request =
                requestFactory.buildPostRequest(getTokenServerUrl(), new UrlEncodedContent(this));
        request.setParser(new JsonObjectParser(getJsonFactory()));
        request.setThrowExceptionOnExecuteError(false);
        HttpResponse response = request.execute();
        if (response.isSuccessStatusCode()) {
            if (!HttpResponseUtils.hasMessageBody(response)) {
                return null;
            }
            // check and see if status code is 200 but has error response
            String responseContent = HttpResponseUtils.parseAsStringWithoutClosing(response);
            TokenResponse tokenResponse = response
                    .getRequest()
                    .getParser()
                    .parseAndClose(new StringReader(responseContent), TokenResponse.class);
            if (tokenResponse.containsKey("error")) {
                throw LenientTokenResponseException.from(getJsonFactory(), response,
                        responseContent);
            }
            return response.getRequest().getParser()
                    .parseAndClose(new StringReader(responseContent), TokenResponse.class);
        }
        throw TokenResponseException.from(getJsonFactory(), response);
    }

}
