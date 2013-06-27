
package com.wuman.android.auth;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.CredentialStoreRefreshListener;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;
import com.google.api.client.util.Clock;
import com.wuman.android.auth.oauth.OAuthHmacCredential;
import com.wuman.android.auth.oauth2.explicit.LenientAuthorizationCodeTokenRequest;
import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Thread-safe OAuth 1.0a and 2.0 authorization flow that manages and persists
 * end-user credentials. Both explicit authorization and implicit authorization
 * of OAuth 2.0 are supported.
 * <p>
 * This is designed to simplify the flow in which an end-user authorizes the
 * application to access their protected data, and then the application has
 * access to their data based on an access token and a refresh token to refresh
 * that access token when it expires.
 * </p>
 * <p>
 * The first step is to call {@link #loadCredential(String)} based on the known
 * user ID to check if the end-user's credentials are already known. If not,
 * call {@link #newExplicitAuthorizationUrl()} (or
 * {@link #newImplicitAuthorizationUrl()}) and direct the end-user's browser to
 * an authorization page. If explicit authorization is used, the web browser
 * will then redirect to the redirect URL with a {@code "code"} query parameter
 * which can then be used to request an access token using
 * {@link #newTokenRequest(String)}; If implicit authorization is used, the web
 * browser will then redirect to the redirect URL with a {@code "access_token"}
 * fragment. The implicit redirect URL is returned as
 * {@link ImplicitResponseUrl}. Finally, use
 * {@link #createAndStoreCredential(TokenResponse, String)} or
 * {@link #createAndStoreCredential(ImplicitResponseUrl, String)} to store and
 * obtain a credential for accessing protected resources.
 * </p>
 * 
 * @author David Wu
 */
public class AuthorizationFlow extends AuthorizationCodeFlow {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    /** Credential created listener or {@code null} for none. */
    private final CredentialCreatedListener credentialCreatedListener;

    /** Temporary token request URL */
    private String temporaryTokenRequestUrl;

    /**
     * Listener for a created credential after a successful token response in
     * {@link AuthorizationFlow#createAndStoreCredential(OAuthCredentialsResponse, String)}
     * ,
     * {@link AuthorizationFlow#createAndStoreCredential(TokenResponse, String)}
     * or
     * {@link AuthorizationFlow#createAndStoreCredential(ImplicitResponseUrl, String)}
     * . .
     */
    public interface CredentialCreatedListener extends
            com.google.api.client.auth.oauth2.AuthorizationCodeFlow.CredentialCreatedListener {

        /**
         * Notifies of a created credential after a successful token response in
         * {@link AuthorizationFlow#createAndStoreCredential(ImplicitResponseUrl, String)}
         * .
         * <p>
         * Typical use is to parse additional fields from the credential
         * created, such as an ID token.
         * </p>
         * 
         * @param credential created credential
         * @param implicitResponse successful implicit response URL
         */
        void onCredentialCreated(Credential credential, ImplicitResponseUrl implicitResponse)
                throws IOException;

        /**
         * Notifies of a created credential after a successful token response in
         * {@link AuthorizationFlow#createAndStoreCredential(OAuthCredentialsResponse, String)}
         * 
         * @param credential
         * @param oauth10aResponse
         * @throws IOException
         */
        void onCredentialCreated(Credential credential, OAuthCredentialsResponse oauth10aResponse)
                throws IOException;
    }

    AuthorizationFlow(Builder builder) {
        super(builder);
        credentialCreatedListener = builder.getGeneralCredentialCreatedListener();
        temporaryTokenRequestUrl = builder.getTemporaryTokenRequestUrl();
    }

    /**
     * Returns the Request Token URL in OAuth 1.0a.
     * 
     * @return
     */
    public final String getTemporaryTokenRequestUrl() {
        return temporaryTokenRequestUrl;
    }

    /**
     * Loads the OAuth 1.0a credential of the given user ID from the credential
     * store.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @return OAuth 1.0a credential found in the credential store of the given
     *         user ID or {@code null} for none found
     */
    public OAuthHmacCredential load10aCredential(String userId) throws IOException {
        if (getCredentialStore() == null) {
            return null;
        }
        OAuthHmacCredential credential = new10aCredential(userId);
        if (!getCredentialStore().load(userId, credential)) {
            return null;
        }
        return credential;
    }

    /**
     * Returns the response of a Request Token request as defined in <a
     * href="http://oauth.net/core/1.0a/#auth_step1">Obtaining an Unauthorized
     * Request Token</a>.
     * 
     * @param redirectUri the {@code oauth_callback} as defined in <a
     *            href="http://oauth.net/core/1.0a/#rfc.section.6.1.1">Consumer
     *            Obtains a Request Token</a>
     * @return
     * @throws IOException
     */
    public OAuthCredentialsResponse new10aTemporaryTokenRequest(String redirectUri)
            throws IOException {
        OAuthGetTemporaryToken temporaryToken =
                new OAuthGetTemporaryToken(getTemporaryTokenRequestUrl());
        OAuthHmacSigner signer = new OAuthHmacSigner();
        ClientParametersAuthentication clientAuthentication = (ClientParametersAuthentication) getClientAuthentication();
        signer.clientSharedSecret = clientAuthentication.getClientSecret();
        temporaryToken.signer = signer;
        temporaryToken.consumerKey = clientAuthentication.getClientId();
        temporaryToken.callback = redirectUri;
        temporaryToken.transport = getTransport();
        return temporaryToken.execute();
    }

    /**
     * Returns a new instance of a temporary token authorization request URL as
     * defined in <a
     * href="http://oauth.net/core/1.0a/#rfc.section.6.2.1">Consumer Directs the
     * User to the Service Provider</a>.
     * 
     * @param temporaryToken
     * @return
     */
    public OAuthAuthorizeTemporaryTokenUrl new10aAuthorizationUrl(String temporaryToken) {
        OAuthAuthorizeTemporaryTokenUrl authorizationUrl =
                new OAuthAuthorizeTemporaryTokenUrl(getAuthorizationServerEncodedUrl());
        authorizationUrl.temporaryToken = temporaryToken;
        return authorizationUrl;
    }

    /**
     * Returns a new instance of a token request based on the given verifier
     * code. This step is defined in <a
     * href="http://oauth.net/core/1.0a/#auth_step3">Obtaining an Access
     * Token</a>.
     * 
     * @param temporaryCredentials
     * @param verifierCode
     * @return
     */
    public OAuthGetAccessToken new10aTokenRequest(OAuthCredentialsResponse temporaryCredentials,
            String verifierCode) {
        OAuthGetAccessToken request = new OAuthGetAccessToken(getTokenServerEncodedUrl());
        request.temporaryToken = temporaryCredentials.token;
        request.transport = getTransport();

        OAuthHmacSigner signer = new OAuthHmacSigner();
        ClientParametersAuthentication clientAuthentication = (ClientParametersAuthentication) getClientAuthentication();
        signer.clientSharedSecret = clientAuthentication.getClientSecret();
        signer.tokenSharedSecret = temporaryCredentials.tokenSecret;

        request.signer = signer;
        request.consumerKey = clientAuthentication.getClientId();
        request.verifier = verifierCode;
        return request;
    }

    @Override
    public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
        return new LenientAuthorizationCodeTokenRequest(getTransport(), getJsonFactory(),
                new GenericUrl(getTokenServerEncodedUrl()), authorizationCode)
                .setClientAuthentication(getClientAuthentication())
                .setRequestInitializer(getRequestInitializer())
                .setScopes(getScopes())
                .setRequestInitializer(
                        new HttpRequestInitializer() {
                            @Override
                            public void initialize(HttpRequest request) throws IOException {
                                request.getHeaders().setAccept("application/json");
                            }
                        });
    }

    /**
     * Returns a new instance of an explicit authorization code request URL.
     * <p>
     * This is a builder for an authorization web page to allow the end user to
     * authorize the application to access their protected resources and that
     * returns an authorization code. It uses the
     * {@link #getAuthorizationServerEncodedUrl()}, {@link #getClientId()}, and
     * {@link #getScopes()}. Sample usage:
     * </p>
     * 
     * <pre>
     * private AuthorizationFlow flow;
     * 
     * public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
     *     String url = flow.newExplicitAuthorizationUrl().setState(&quot;xyz&quot;)
     *             .setRedirectUri(&quot;https://client.example.com/rd&quot;).build();
     *     response.sendRedirect(url);
     * }
     * </pre>
     */
    public AuthorizationCodeRequestUrl newExplicitAuthorizationUrl() {
        return newAuthorizationUrl();
    }

    /**
     * Returns a new instance of an implicit authorization request URL.
     * <p>
     * This is a builder for an authorization web page to allow the end user to
     * authorize the application to access their protected resources and that
     * returns an access token. It uses the
     * {@link #getAuthorizationServerEncodedUrl()}, {@link #getClientId()}, and
     * {@link #getScopes()}. Sample usage:
     * </p>
     * 
     * <pre>
     * private AuthorizationFlow flow;
     * 
     * public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
     *     String url = flow.newImplicitAuthorizationUrl().setState(&quot;xyz&quot;)
     *             .setRedirectUri(&quot;https://client.example.com/rd&quot;).build();
     *     response.sendRedirect(url);
     * }
     * </pre>
     */
    public BrowserClientRequestUrl newImplicitAuthorizationUrl() {
        return new BrowserClientRequestUrl(getAuthorizationServerEncodedUrl(), getClientId())
                .setScopes(getScopes());
    }

    /**
     * Creates a new credential for the given user ID based on the given token
     * response and store in the credential store.
     * 
     * @param response OAuth 1.0a authorization token response
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @return newly created credential
     * @throws IOException
     */
    public OAuthHmacCredential createAndStoreCredential(OAuthCredentialsResponse response,
            String userId) throws IOException {
        OAuthHmacCredential credential = new10aCredential(userId)
                .setAccessToken(response.token)
                .setTokenSharedSecret(response.tokenSecret);
        CredentialStore credentialStore = getCredentialStore();
        if (credentialStore != null) {
            credentialStore.store(userId, credential);
        }
        if (credentialCreatedListener != null) {
            credentialCreatedListener.onCredentialCreated(credential, response);
        }
        return credential;
    }

    /**
     * Creates a new credential for the given user ID based on the given token
     * response and store in the credential store.
     * 
     * @param response implicit authorization token response
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @return newly created credential
     * @throws IOException
     */
    public Credential createAndStoreCredential(ImplicitResponseUrl implicitResponse, String userId)
            throws IOException {
        Credential credential = newCredential(userId)
                .setAccessToken(implicitResponse.getAccessToken())
                .setExpirationTimeMilliseconds(implicitResponse.getExpiresInSeconds());
        CredentialStore credentialStore = getCredentialStore();
        if (credentialStore != null) {
            credentialStore.store(userId, credential);
        }
        if (credentialCreatedListener != null) {
            credentialCreatedListener.onCredentialCreated(credential, implicitResponse);
        }
        return credential;
    }

    /**
     * Returns a new OAuth 1.0a credential instance based on the given user ID.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     */
    private OAuthHmacCredential new10aCredential(String userId) {
        ClientParametersAuthentication clientAuthentication = (ClientParametersAuthentication) getClientAuthentication();
        OAuthHmacCredential.Builder builder =
                new OAuthHmacCredential.Builder(getMethod(), clientAuthentication.getClientId(),
                        clientAuthentication.getClientSecret())
                        .setTransport(getTransport())
                        .setJsonFactory(getJsonFactory())
                        .setTokenServerEncodedUrl(getTokenServerEncodedUrl())
                        .setClientAuthentication(getClientAuthentication())
                        .setRequestInitializer(getRequestInitializer())
                        .setClock(getClock());
        if (getCredentialStore() != null) {
            builder.addRefreshListener(
                    new CredentialStoreRefreshListener(userId, getCredentialStore()));
        }

        builder.getRefreshListeners().addAll(getRefreshListeners());

        return builder.build();
    }

    /**
     * Returns a new OAuth 2.0 credential instance based on the given user ID.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     */
    private Credential newCredential(String userId) {
        Credential.Builder builder = new Credential.Builder(getMethod())
                .setTransport(getTransport())
                .setJsonFactory(getJsonFactory())
                .setTokenServerEncodedUrl(getTokenServerEncodedUrl())
                .setClientAuthentication(getClientAuthentication())
                .setRequestInitializer(getRequestInitializer())
                .setClock(getClock());
        if (getCredentialStore() != null) {
            builder.addRefreshListener(
                    new CredentialStoreRefreshListener(userId, getCredentialStore()));
        }

        builder.getRefreshListeners().addAll(getRefreshListeners());

        return builder.build();
    }

    /**
     * Authorization flow builder.
     * <p>
     * Implementation is not thread-safe.
     * </p>
     */
    public static class Builder extends
            com.google.api.client.auth.oauth2.AuthorizationCodeFlow.Builder {

        /** Credential created listener or {@code null} for none. */
        CredentialCreatedListener credentialCreatedListener;

        /** Temporary token request URL */
        String temporaryTokenRequestUrl;

        /**
         * @param method method of presenting the access token to the resource
         *            server (for example
         *            {@link BearerToken#authorizationHeaderAccessMethod})
         * @param transport HTTP transport
         * @param jsonFactory JSON factory
         * @param tokenServerUrl token server URL
         * @param clientAuthentication client authentication or {@code null} for
         *            none (see
         *            {@link TokenRequest#setClientAuthentication(HttpExecuteInterceptor)}
         *            )
         * @param clientId client identifier
         * @param authorizationServerEncodedUrl authorization server encoded URL
         */
        public Builder(AccessMethod method,
                HttpTransport transport,
                JsonFactory jsonFactory,
                GenericUrl tokenServerUrl,
                HttpExecuteInterceptor clientAuthentication,
                String clientId,
                String authorizationServerEncodedUrl) {
            super(method,
                    transport,
                    jsonFactory,
                    tokenServerUrl,
                    clientAuthentication,
                    clientId,
                    authorizationServerEncodedUrl);
        }

        /**
         * Returns a new instance of an authorization flow based on this
         * builder.
         */
        public AuthorizationFlow build() {
            return new AuthorizationFlow(this);
        }

        /**
         * Sets the temporary token request URL.
         * 
         * @param temporaryTokenRequestUrl
         * @return
         */
        public Builder setTemporaryTokenRequestUrl(String temporaryTokenRequestUrl) {
            this.temporaryTokenRequestUrl = temporaryTokenRequestUrl;
            return this;
        }

        /**
         * Returns the temporary token request URL.
         * 
         * @return
         */
        public String getTemporaryTokenRequestUrl() {
            return temporaryTokenRequestUrl;
        }

        @Override
        public Builder setMethod(AccessMethod method) {
            return (Builder) super.setMethod(method);
        }

        @Override
        public Builder setTransport(HttpTransport transport) {
            return (Builder) super.setTransport(transport);
        }

        @Override
        public Builder setJsonFactory(JsonFactory jsonFactory) {
            return (Builder) super.setJsonFactory(jsonFactory);
        }

        @Override
        public Builder setTokenServerUrl(GenericUrl tokenServerUrl) {
            return (Builder) super.setTokenServerUrl(tokenServerUrl);
        }

        @Override
        public Builder setClientAuthentication(HttpExecuteInterceptor clientAuthentication) {
            return (Builder) super.setClientAuthentication(clientAuthentication);
        }

        @Override
        public Builder setClientId(String clientId) {
            return (Builder) super.setClientId(clientId);
        }

        @Override
        public Builder setAuthorizationServerEncodedUrl(String authorizationServerEncodedUrl) {
            return (Builder) super.setAuthorizationServerEncodedUrl(authorizationServerEncodedUrl);
        }

        @Override
        public Builder setClock(Clock clock) {
            return (Builder) super.setClock(clock);
        }

        @Beta
        @Override
        public Builder setCredentialStore(CredentialStore credentialStore) {
            return (Builder) super.setCredentialStore(credentialStore);
        }

        @Override
        public Builder setRequestInitializer(HttpRequestInitializer requestInitializer) {
            return (Builder) super.setRequestInitializer(requestInitializer);
        }

        @Beta
        @Deprecated
        @Override
        public Builder setScopes(Iterable<String> scopes) {
            return (Builder) super.setScopes(scopes);
        }

        @Beta
        @Deprecated
        @Override
        public Builder setScopes(String... scopes) {
            return (Builder) super.setScopes(scopes);
        }

        @Override
        public Builder setScopes(Collection<String> scopes) {
            return (Builder) super.setScopes(scopes);
        }

        /**
         * Sets the credential created listener or {@code null} for none. *
         * <p>
         * Overriding is only supported for the purpose of calling the super
         * implementation and changing the return type, but nothing else.
         * </p>
         */
        public Builder setCredentialCreatedListener(
                CredentialCreatedListener credentialCreatedListener) {
            this.credentialCreatedListener = credentialCreatedListener;
            return (Builder) super.setCredentialCreatedListener(credentialCreatedListener);
        }

        /**
         * Returns the credential created listener or {@code null} for none.
         */
        public final CredentialCreatedListener getGeneralCredentialCreatedListener() {
            return credentialCreatedListener;
        }

        @Override
        public Builder addRefreshListener(CredentialRefreshListener refreshListener) {
            return (Builder) super.addRefreshListener(refreshListener);
        }

        @Override
        public Builder setRefreshListeners(Collection<CredentialRefreshListener> refreshListeners) {
            return (Builder) super.setRefreshListeners(refreshListeners);
        }

    }

}
