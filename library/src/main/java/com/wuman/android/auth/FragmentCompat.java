
package com.wuman.android.auth;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class FragmentCompat {

    private android.support.v4.app.Fragment supportFragment;
    private android.app.Fragment nativeFragment;

    FragmentCompat(android.support.v4.app.Fragment supportFragment) {
        super();
        this.supportFragment = supportFragment;
    }

    FragmentCompat(android.app.Fragment nativeFragment) {
        super();
        this.nativeFragment = nativeFragment;
    }

    static {
        // hack to force ProGuard to consider newInstance() used, since
        // otherwise it would be stripped out
        FragmentCompat.newInstance(null);
    }

    static FragmentCompat newInstance(Object fragment) {
        if (fragment == null) {
            return null;
        } else if (fragment instanceof android.support.v4.app.Fragment) {
            return new FragmentCompat((android.support.v4.app.Fragment) fragment);
        } else if (fragment instanceof android.app.Fragment) {
            return new FragmentCompat((android.app.Fragment) fragment);
        } else {
            return null;
        }
    }

    Object getFragment() {
        return supportFragment != null ? supportFragment : nativeFragment;
    }

    View getView() {
        if (supportFragment != null) {
            return supportFragment.getView();
        } else {
            return nativeFragment.getView();
        }
    }

    boolean isAdded() {
        if (supportFragment != null) {
            return supportFragment.isAdded();
        } else {
            return nativeFragment.isAdded();
        }
    }

    boolean isRemoving() {
        if (supportFragment != null) {
            return supportFragment.isRemoving();
        } else {
            return nativeFragment.isRemoving();
        }
    }

    void setArguments(Bundle args) {
        if (supportFragment != null) {
            supportFragment.setArguments(args);
        } else {
            nativeFragment.setArguments(args);
        }
    }

    Bundle getArguments() {
        if (supportFragment != null) {
            return supportFragment.getArguments();
        } else {
            return nativeFragment.getArguments();
        }
    }

    View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return null;
    }

    void onViewCreated(View view, Bundle savedInstanceState) {
    }

    void onActivityCreated(Bundle savedInstanceState) {
    }

    void onDestroy() {
    }

}
