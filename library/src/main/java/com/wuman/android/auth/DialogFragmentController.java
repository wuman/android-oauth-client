
package com.wuman.android.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Preconditions;
import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public abstract class DialogFragmentController implements AuthorizationDialogController {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    private static final String FRAGMENT_TAG = "oauth_dialog";

    /** A boolean to indicate if the dialog fragment needs to be full screen **/
    public final boolean fullScreen;

    /** A boolean to indicate if the dialog fragment needs to be horizontal **/
    public final boolean horizontalProgress;

    public final boolean hideFullScreenTitle;

    private final FragmentManagerCompat fragmentManager;

    /** {@link Handler} for running UI in the main thread. */
    private final Handler uiHandler;

    /**
     * Verification code (for explicit authorization) or access token (for
     * implicit authorization) or {@code null} for none.
     */
    String codeOrToken;

    /** Error code or {@code null} for none. */
    String error;

    /** Implicit response URL. */
    ImplicitResponseUrl implicitResponseUrl;

    /** Lock on the code and error. */
    final Lock lock = new ReentrantLock();

    /** Condition for receiving an authorization response. */
    final Condition gotAuthorizationResponse = lock.newCondition();

    /**
     * @param fragmentManager
     */
    public DialogFragmentController(android.support.v4.app.FragmentManager fragmentManager) {
        this(fragmentManager, false);
    }

    /**
     * @param fragmentManager
     */
    public DialogFragmentController(android.app.FragmentManager fragmentManager) {
        this(fragmentManager, false);
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     */
    public DialogFragmentController(android.support.v4.app.FragmentManager fragmentManager, boolean fullScreen) {
        this(fragmentManager, fullScreen, false);
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     */
    public DialogFragmentController(android.app.FragmentManager fragmentManager, boolean fullScreen) {
        this(fragmentManager, fullScreen, false);
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     * @param horizontalProgress
     */
    public DialogFragmentController(android.support.v4.app.FragmentManager fragmentManager, boolean fullScreen,
        boolean horizontalProgress) {
        this(fragmentManager, fullScreen, horizontalProgress, false);
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     * @param horizontalProgress
     */
    public DialogFragmentController(android.app.FragmentManager fragmentManager, boolean fullScreen,
        boolean horizontalProgress) {
        this(fragmentManager, fullScreen, horizontalProgress, false);
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     * @param horizontalProgress
     * @param hideFullScreenTitle if you set this flag to true, {@param horizontalProgress} will be ignored
     */
    public DialogFragmentController(android.support.v4.app.FragmentManager fragmentManager, boolean fullScreen,
        boolean horizontalProgress, boolean hideFullScreenTitle) {
        super();
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.fragmentManager =
                new FragmentManagerCompat(Preconditions.checkNotNull(fragmentManager));
        this.fullScreen = fullScreen;
        this.horizontalProgress = horizontalProgress;
        this.hideFullScreenTitle = hideFullScreenTitle;
    }

    /**
     *
     * @param fragmentManager
     * @param fullScreen
     * @param horizontalProgress
     * @param hideFullScreenTitle if you set this flag to true, {@param horizontalProgress} will be ignored
     */
    public DialogFragmentController(android.app.FragmentManager fragmentManager, boolean fullScreen,
        boolean horizontalProgress, boolean hideFullScreenTitle) {
        super();
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.fragmentManager =
                new FragmentManagerCompat(Preconditions.checkNotNull(fragmentManager));
        this.fullScreen = fullScreen;
        this.horizontalProgress = horizontalProgress;
        this.hideFullScreenTitle = hideFullScreenTitle;
    }

    Object getFragmentManager() {
        return this.fragmentManager.getFragmentManager();
    }

    /**
     * Executes the {@link Runnable} on the main thread.
     *
     * @param runnable
     */
    private void runOnMainThread(Runnable runnable) {
        uiHandler.post(runnable);
    }

    private void dismissDialog() {
        runOnMainThread(new Runnable() {
            public void run() {
                DialogFragmentCompat frag = fragmentManager
                        .findFragmentByTag(DialogFragmentCompat.class, FRAGMENT_TAG);
                if (frag != null) {
                    frag.dismiss();
                }
            }
        });
    }

    @Override
    public void set(String codeOrToken, String error, ImplicitResponseUrl implicitResponseUrl,
            boolean signal) {
        lock.lock();
        try {
            this.error = error;
            this.codeOrToken = codeOrToken;
            this.implicitResponseUrl = implicitResponseUrl;
            if (signal) {
                gotAuthorizationResponse.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void requestAuthorization(OAuthAuthorizeTemporaryTokenUrl authorizationRequestUrl) {
        internalRequestAuthorization(authorizationRequestUrl);
    }

    @Override
    public void requestAuthorization(AuthorizationCodeRequestUrl authorizationRequestUrl) {
        internalRequestAuthorization(authorizationRequestUrl);
    }

    @Override
    public void requestAuthorization(BrowserClientRequestUrl authorizationRequestUrl) {
        internalRequestAuthorization(authorizationRequestUrl);
    }

    private void internalRequestAuthorization(final GenericUrl authorizationRequestUrl) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if(fragmentManager.isDestroyed()) {
                    return;
                }
                FragmentTransactionCompat ft = fragmentManager.beginTransaction();
                FragmentCompat prevDialog =
                        fragmentManager.findFragmentByTag(FragmentCompat.class, FRAGMENT_TAG);
                if (prevDialog != null) {
                    ft.remove(prevDialog);
                }
                DialogFragmentCompat frag =
                        OAuthDialogFragment.newInstance(
                                authorizationRequestUrl,
                                DialogFragmentController.this);
                frag.show(ft, FRAGMENT_TAG);
            }
        });
    }

    @Override
    public String waitForVerifierCode() throws IOException {
        return waitForExplicitCode();
    }

    @Override
    public String waitForExplicitCode() throws IOException {
        lock.lock();
        try {
            while (codeOrToken == null && error == null) {
                gotAuthorizationResponse.awaitUninterruptibly();
            }
            dismissDialog();
            if (error != null) {
                if (TextUtils.equals(ERROR_USER_CANCELLED, error)) {
                    throw new CancellationException("User authorization failed (" + error + ")");
                } else {
                    throw new IOException("User authorization failed (" + error + ")");
                }
            }
            return codeOrToken;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ImplicitResponseUrl waitForImplicitResponseUrl() throws IOException {
        lock.lock();
        try {
            while (codeOrToken == null && error == null) {
                gotAuthorizationResponse.awaitUninterruptibly();
            }
            dismissDialog();
            if (error != null) {
                if (TextUtils.equals(ERROR_USER_CANCELLED, error)) {
                    throw new CancellationException("User authorization failed (" + error + ")");
                } else {
                    throw new IOException("User authorization failed (" + error + ")");
                }
            }
            return implicitResponseUrl;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() throws IOException {
        set(null, null, null, true);
        dismissDialog();
    }

    @Override
    public void onPrepareDialog(Dialog dialog) {
        // do nothing
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // use default implementation in DialogFragment
        return null;
    }

    @Override
    public boolean setProgressShown(String url, View view, int newProgress) {
        // use default implementation in DialogFragment
        return false;
    }

}
