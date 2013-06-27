
package com.wuman.oauth.samples.github.api.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.wuman.oauth.samples.github.api.LinkHeaderParser;

public class Pagination extends GenericData {

    @Key("next")
    private String next;

    @Key("last")
    private String last;

    @Key("first")
    private String first;

    @Key("prev")
    private String prev;

    public Pagination(String linkHeaderValue) {
        super();
        LinkHeaderParser.parse(linkHeaderValue, this);
    }

    public final String getNext() {
        return next;
    }

    public final String getLast() {
        return last;
    }

    public final String getFirst() {
        return first;
    }

    public final String getPrev() {
        return prev;
    }

    @Override
    public Pagination set(String fieldName, Object value) {
        return (Pagination) super.set(fieldName, value);
    }

    @Override
    public Pagination clone() {
        return (Pagination) super.clone();
    }

    @Override
    public String toString() {
        return "next:" + next + ",last:" + last + ",first:" + first + ",prev:" + prev;
    }

}
