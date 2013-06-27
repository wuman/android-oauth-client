/*
 * Copyright (c) 2010 Google Inc.
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
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;

/**
 * Exception thrown when receiving an error response from the token server as
 * specified in <a href="http://tools.ietf.org/html/rfc6749#section-5.2">Error
 * Response</a>
 * <p>
 * To get the structured details, use {@link #getDetails()}.
 * </p>
 * <p>
 * Sample usage can be found for {@link AuthorizationCodeTokenRequest}.
 * </p>
 * 
 * @since 1.7
 * @author Yaniv Inbar
 */
public class LenientTokenResponseException extends HttpResponseException {

    private static final long serialVersionUID = 7709534258323056472L;

    /** Token error response details or {@code null} if unable to parse. */
    private final transient TokenErrorResponse details;

    /**
     * @param builder builder
     * @param details token error response details or {@code null} if unable to
     *            parse
     */
    LenientTokenResponseException(Builder builder, TokenErrorResponse details) {
        super(builder);
        this.details = details;
    }

    /**
     * Returns the token error response details or {@code null} if unable to
     * parse.
     */
    public final TokenErrorResponse getDetails() {
        return details;
    }

    /**
     * Returns a new instance of {@link LenientTokenResponseException}.
     * <p>
     * If there is a JSON error response, it is parsed using
     * {@link TokenErrorResponse}, which can be inspected using
     * {@link #getDetails()}. Otherwise, the full response content is read and
     * included in the exception message.
     * </p>
     * 
     * @param jsonFactory JSON factory
     * @param readResponse an HTTP response that has already been read
     * @param responseContent the content String of the HTTP response
     * @return new instance of {@link TokenErrorResponse}
     */
    public static LenientTokenResponseException from(JsonFactory jsonFactory,
            HttpResponse readResponse, String responseContent) {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(
                readResponse.getStatusCode(), readResponse.getStatusMessage(),
                readResponse.getHeaders());
        // details
        Preconditions.checkNotNull(jsonFactory);
        TokenErrorResponse details = null;
        String detailString = null;
        String contentType = readResponse.getContentType();
        try {
            if (/* !response.isSuccessStatusCode() && */true
                    && contentType != null
                    && HttpMediaType.equalsIgnoreParameters(Json.MEDIA_TYPE, contentType)) {
                details = readResponse
                        .getRequest()
                        .getParser()
                        .parseAndClose(new StringReader(responseContent), TokenErrorResponse.class);
                detailString = details.toPrettyString();
            } else {
                detailString = responseContent;
            }
        } catch (IOException exception) {
            // it would be bad to throw an exception while throwing an exception
            exception.printStackTrace();
        }
        // message
        StringBuilder message = HttpResponseException.computeMessageBuffer(readResponse);
        if (!com.google.api.client.util.Strings.isNullOrEmpty(detailString)) {
            message.append(StringUtils.LINE_SEPARATOR).append(detailString);
            builder.setContent(detailString);
        }
        builder.setMessage(message.toString());
        return new LenientTokenResponseException(builder, details);
    }

}
