
package com.wuman.android.auth;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;
import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.io.IOException;

/**
 * {@link AuthorizationUIController} controls the UI associated with each step
 * of the OAuth authorization flow as defined by {@link AuthorizationFlow}.
 * 
 * @author David Wu
 */
public interface AuthorizationUIController {

    /**
     * Error indicating that the user has cancelled the authorization process,
     * most likely due to cancellation of the authorization dialog.
     */
    String ERROR_USER_CANCELLED = "user_cancelled";

    /**
     * The request is missing a required parameter, includes an invalid
     * parameter value, includes a parameter more than once, or is otherwise
     * malformed.
     */
    String ERROR_INVALID_REQUEST = "invalid_request";

    /**
     * The client is not authorized to request an authorization code using this
     * method.
     */
    String ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client";

    /**
     * The resource owner or authorization server denied the request.
     */
    String ERROR_ACCESS_DENIED = "access_denied";

    /**
     * The authorization server does not support obtaining an authorization code
     * using this method.
     */
    String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

    /** The requested scope is invalid, unknown, or malformed. */
    String ERROR_INVALID_SCOPE = "invalid_scope";

    /**
     * The authorization server encountered an unexpected condition that
     * prevented it from fulfilling the request. (This error code is needed
     * because a 500 Internal Server Error HTTP status code cannot be returned
     * to the client via an HTTP redirect.)
     */
    String ERROR_SERVER_ERROR = "server_error";

    /**
     * The authorization server is currently unable to handle the request due to
     * a temporary overloading or maintenance of the server. (This error code is
     * needed because a 503 Service Unavailable HTTP status code cannot be
     * returned to the client via an HTTP redirect.)
     */
    String ERROR_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";

    /**
     * Handles user authorization by redirecting to the OAuth 1.0a authorization
     * server as defined in <a
     * href="http://oauth.net/core/1.0a/#auth_step2">Obtaining User
     * Authorization</a>.
     * 
     * @param authorizationRequestUrl
     */
    void requestAuthorization(OAuthAuthorizeTemporaryTokenUrl authorizationRequestUrl);

    /**
     * Handles user authorization by redirecting to the OAuth 2.0 authorization
     * server as defined in <a
     * href="http://tools.ietf.org/html/rfc6749#section-4.1.1">Authorization
     * Request</a>.
     * 
     * @param authorizationRequestUrl
     */
    void requestAuthorization(AuthorizationCodeRequestUrl authorizationRequestUrl);

    /**
     * Handles user authorization by redirecting to the OAuth 2.0 authorization
     * server as defined in <a
     * href="http://tools.ietf.org/html/rfc6749#section-4.2.1">Authorization
     * Request</a>.
     * 
     * @param authorizationRequestUrl
     */
    void requestAuthorization(BrowserClientRequestUrl authorizationRequestUrl);

    /** Returns the redirect URI. */
    String getRedirectUri() throws IOException;

    /** Waits for OAuth 1.0a verifier code. */
    String waitForVerifierCode() throws IOException;

    /** Waits for OAuth 2.0 explicit authorization code. */
    String waitForExplicitCode() throws IOException;

    /** Waits for OAuth 2.0 implicit access token response. */
    ImplicitResponseUrl waitForImplicitResponseUrl() throws IOException;

    /** Releases any resources and stops any processes started. */
    void stop() throws IOException;

}
