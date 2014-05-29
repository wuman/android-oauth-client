
package com.wuman.android.auth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class FragmentManagerCompat {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    private android.support.v4.app.FragmentManager supportFragmentManager;
    private android.app.FragmentManager nativeFragmentManager;

    FragmentManagerCompat(FragmentManager nativeFragmentManager) {
        super();
        this.nativeFragmentManager = nativeFragmentManager;
    }

    FragmentManagerCompat(android.support.v4.app.FragmentManager supportFragmentManager) {
        super();
        this.supportFragmentManager = supportFragmentManager;
    }

    Object getFragmentManager() {
        return supportFragmentManager != null ? supportFragmentManager : nativeFragmentManager;
    }

    boolean isDestroyed() {
        if (supportFragmentManager != null) {
            return supportFragmentManager.isDestroyed();
        } else {
            return nativeFragmentManager.isDestroyed();
        }
    }

    @SuppressLint("CommitTransaction")
    FragmentTransactionCompat beginTransaction() {
        if (supportFragmentManager != null) {
            return new FragmentTransactionCompat(supportFragmentManager.beginTransaction());
        } else {
            return new FragmentTransactionCompat(nativeFragmentManager.beginTransaction());
        }
    }

    @SuppressWarnings("unchecked")
    <T extends FragmentCompat> T findFragmentByTag(Class<T> fragmentClass, String tag) {
        try {
            Object fragment = null;
            if (supportFragmentManager != null) {
                fragment = supportFragmentManager.findFragmentByTag(tag);
            } else {
                fragment = nativeFragmentManager.findFragmentByTag(tag);
            }
            if (fragment == null) {
                return null;
            }
            return (T) fragmentClass
                    .getDeclaredMethod("newInstance", Object.class)
                    .invoke(null, fragment);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Unable to perform findFragmentByTag()", e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Unable to perform findFragmentByTag()", e);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.WARNING, "Unable to perform findFragmentByTag()", e);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, "Unable to perform findFragmentByTag()", e);
        }
        return null;
    }
}
