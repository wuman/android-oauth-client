
package com.wuman.android.auth;

import android.annotation.TargetApi;
import android.os.Build;

class FragmentTransactionCompat {

    private final android.app.FragmentTransaction nativeFragmentTransaction;
    private final android.support.v4.app.FragmentTransaction supportFragmentTransaction;

    FragmentTransactionCompat(android.app.FragmentTransaction nativeFragmentTransaction) {
        super();
        this.nativeFragmentTransaction = nativeFragmentTransaction;
        this.supportFragmentTransaction = null;
    }

    FragmentTransactionCompat(android.support.v4.app.FragmentTransaction supportFragmentTransaction) {
        super();
        this.supportFragmentTransaction = supportFragmentTransaction;
        this.nativeFragmentTransaction = null;
    }

    Object getFragmentTransaction() {
        return supportFragmentTransaction != null ?
                supportFragmentTransaction : nativeFragmentTransaction;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    FragmentTransactionCompat remove(FragmentCompat fragment) {
        if (supportFragmentTransaction != null) {
            supportFragmentTransaction
                    .remove((android.support.v4.app.Fragment) fragment.getFragment());
        } else {
            nativeFragmentTransaction.remove((android.app.Fragment) fragment.getFragment());
        }
        return this;
    }

}
