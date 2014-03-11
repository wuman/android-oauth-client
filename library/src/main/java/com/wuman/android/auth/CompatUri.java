
package com.wuman.android.auth;

import android.net.Uri;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

class CompatUri {

    private static final String NOT_HIERARCHICAL = "This isn't a hierarchical URI.";

    static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

}
