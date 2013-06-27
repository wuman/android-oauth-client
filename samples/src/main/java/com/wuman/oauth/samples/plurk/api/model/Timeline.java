
package com.wuman.oauth.samples.plurk.api.model;

import com.google.api.client.util.Key;

import java.util.List;
import java.util.Map;

public class Timeline extends PlurkResponse {

    @Key("plurks")
    private List<Plurk> plurks;

    @Key("plurk_users")
    private Map<String, User> users;

    public final List<Plurk> getPlurks() {
        return plurks;
    }

    public final Map<String, User> getUsers() {
        return users;
    }

    @Override
    public Timeline clone() {
        return (Timeline) super.clone();
    }

    @Override
    public Timeline set(String fieldName, Object value) {
        return (Timeline) super.set(fieldName, value);
    }

}
