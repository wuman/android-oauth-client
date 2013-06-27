
package com.wuman.android.auth.oauth2.store;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

/**
 * {@link Beta} <br/>
 * Thread-safe {@link SharedPreferences} implementation of a credential store.
 * 
 * @author David Wu
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SharedPreferencesCredentialStore implements CredentialStore {

    /** Json factory for serializing user credentials. */
    private final JsonFactory jsonFactory;

    private final SharedPreferences prefs;

    /**
     * @param context Context in which to store user credentials
     * @param name Name by which the SharedPreferences file is stored as
     * @param jsonFactory JSON factory to serialize user credentials
     */
    public SharedPreferencesCredentialStore(Context context, String name, JsonFactory jsonFactory) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(name);
        this.prefs = Preconditions.checkNotNull(
                context.getSharedPreferences(name, Context.MODE_PRIVATE));
        this.jsonFactory = Preconditions.checkNotNull(jsonFactory);
    }

    @Override
    public boolean load(String userId, Credential credential) throws IOException {
        Preconditions.checkNotNull(userId);
        String credentialJson = prefs.getString(userId, null);
        if (TextUtils.isEmpty(credentialJson)) {
            return false;
        }
        FilePersistedCredential fileCredential = jsonFactory.fromString(
                credentialJson, FilePersistedCredential.class);
        if (fileCredential == null) {
            return false;
        }
        fileCredential.load(credential);
        return true;
    }

    @Override
    public void store(String userId, Credential credential) throws IOException {
        Preconditions.checkNotNull(userId);
        FilePersistedCredential fileCredential = new FilePersistedCredential();
        fileCredential.store(credential);
        String credentialJson = jsonFactory.toString(fileCredential);
        prefs.edit().putString(userId, credentialJson).apply();
    }

    @Override
    public void delete(String userId, Credential credential) throws IOException {
        Preconditions.checkNotNull(userId);
        prefs.edit().remove(userId).apply();
    }

}
