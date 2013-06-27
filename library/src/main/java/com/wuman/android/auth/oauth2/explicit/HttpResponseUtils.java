
package com.wuman.android.auth.oauth2.explicit;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpResponseUtils {

    static String parseAsStringWithoutClosing(HttpResponse response) throws IOException {
        InputStream content = response.getContent();
        if (content == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(content, out, false);
        return out.toString(response.getContentCharset().name());
    }

    static boolean hasMessageBody(HttpResponse response) throws IOException {
        int statusCode = response.getStatusCode();
        if (response.getRequest().getRequestMethod().equals(HttpMethods.HEAD)
                || statusCode / 100 == 1
                || statusCode == HttpStatusCodes.STATUS_CODE_NO_CONTENT
                || statusCode == HttpStatusCodes.STATUS_CODE_NOT_MODIFIED) {
            response.ignore();
            return false;
        }
        return true;
    }

    private HttpResponseUtils() {
    }

}
