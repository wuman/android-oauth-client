
package com.wuman.android.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link AuthorizationUIController} that redirects the user to the OAuth
 * authorization server via an Android {@link Dialog} with {@link WebView}.
 * 
 * @author David Wu
 */
public interface AuthorizationDialogController extends AuthorizationUIController {

    /**
     * Sets the result that are guarded by a {@link ReentrantLock}.
     * 
     * @param codeOrToken
     * @param error
     * @param implicitResponseUrl
     * @param signal
     */
    void set(String codeOrToken, String error, ImplicitResponseUrl implicitResponseUrl,
            boolean signal);

    /**
     * Prepares the dialog to be displayed. This is called before the dialog is
     * shown. You can use this method to modify the appearance of the dialog.
     * 
     * @param dialog
     */
    void onPrepareDialog(Dialog dialog);

    /**
     * Implement this method to supply the content of the dialog if needed.
     * Returning {@code null} will show the default implementation of dialog
     * content. Any customized implementation must include in its layout a
     * {@link WebView} object with the id {@link android.R.id#primary} and a
     * {@link View} object (most likely {@link ProgressBar}) with the id
     * {@link android.R.id#progress}.
     * 
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    /**
     * Indicate whether Javascript should be enabled for the WebView.
     * 
     * @return {@code true} if Javascript should be enabled, {@code false}
     *         otherwise.
     */
    boolean isJavascriptEnabledForWebView();

    /**
     * Show the progress due to web page loading.
     * 
     * @param url
     * @param view
     * @param current page loading progress, represented by an integer between 0
     *            and 100.
     * @return {@code true} if all UI is handled by the subclass implementation
     *         and the default implementation of the parent class should be
     *         bypassed, {@code false} otherwise.
     */
    boolean setProgressShown(String url, View view, int progress);
}
