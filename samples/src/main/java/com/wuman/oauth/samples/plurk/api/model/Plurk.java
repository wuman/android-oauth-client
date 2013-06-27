
package com.wuman.oauth.samples.plurk.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Plurk extends GenericJson {

    private static final SimpleDateFormat GMT_FORMAT = new SimpleDateFormat(
            "E, dd MMM yyyy HH:mm:ss z", Locale.US);
    static {
        GMT_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Key("qualifier")
    private String qualifier;

    @Key("plurk_id")
    private Long plurkId;

    @Key("owner_id")
    private Long ownerId;

    @Key("posted")
    private String postedRaw;

    private DateTime posted;

    @Key("content")
    private String content;

    @Key("content_raw")
    private String contentRaw;

    private User owner;

    public final User getOwner() {
        return owner;
    }

    public final Plurk setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public final String getQualifier() {
        return qualifier;
    }

    public final Long getId() {
        return plurkId;
    }

    public final Long getOwnerId() {
        return ownerId;
    }

    public final String getContent() {
        return content;
    }

    public final String getContentRaw() {
        return contentRaw;
    }

    public final DateTime getPosted() {
        if (posted != null) {
            return posted;
        }
        try {
            final long timestamp = GMT_FORMAT.parse(postedRaw).getTime();
            posted = new DateTime(timestamp, 0);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
        return posted;
    }

    @Override
    public Plurk clone() {
        return (Plurk) super.clone();
    }

    @Override
    public Plurk set(String fieldName, Object value) {
        return (Plurk) super.set(fieldName, value);
    }

}
