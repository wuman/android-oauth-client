
package com.wuman.android.auth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.http.GenericUrl;
import com.wuman.android.auth.oauth.OAuth10aResponseUrl;
import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

class OAuthDialogFragment extends DialogFragmentCompat {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    private static final String ARG_AUTHORIZATION_REQUEST_URL = "authRequestUrl";
    private static final String ARG_AUTHORIZATION_TYPE = "authType";

    private static final String AUTHORIZATION_10A = "10a";
    private static final String AUTHORIZATION_EXPLICIT = "explicit";
    private static final String AUTHORIZATION_IMPLICIT = "implicit";

    private AuthorizationDialogController mController;
    private boolean mFullScreen;
    private boolean mHorizontalProgress;
    private boolean mHideFullScreenTitle;

    private OAuthDialogFragment(android.app.DialogFragment fragment, boolean fullScreen,
        boolean horizontalProgress, boolean hideFullScreenTitle) {
        super(fragment);
        this.mFullScreen = fullScreen;
        this.mHorizontalProgress = horizontalProgress;
        this.mHideFullScreenTitle = hideFullScreenTitle;
    }

    private OAuthDialogFragment(android.support.v4.app.DialogFragment fragment,
        boolean fullScreen, boolean horizontalProgress, boolean hideFullScreenTitle) {
        super(fragment);
        this.mFullScreen = fullScreen;
        this.mHorizontalProgress = horizontalProgress;
        this.mHideFullScreenTitle = hideFullScreenTitle;
    }

    final void setController(AuthorizationDialogController controller) {
        mController = controller;
    }

    @TargetApi(HONEYCOMB)
    public static final OAuthDialogFragment newInstance(
            GenericUrl authorizationRequestUrl,
            DialogFragmentController controller) {
        Bundle args = new Bundle();
        args.putString(ARG_AUTHORIZATION_REQUEST_URL, authorizationRequestUrl.build());
        if (authorizationRequestUrl instanceof OAuthAuthorizeTemporaryTokenUrl) {
            args.putString(ARG_AUTHORIZATION_TYPE, AUTHORIZATION_10A);
        } else if (authorizationRequestUrl instanceof AuthorizationCodeRequestUrl) {
            args.putString(ARG_AUTHORIZATION_TYPE, AUTHORIZATION_EXPLICIT);
        } else {
            args.putString(ARG_AUTHORIZATION_TYPE, AUTHORIZATION_IMPLICIT);
        }
        BaseDialogFragmentImpl fragImpl;
        OAuthDialogFragment frag;
        if (controller.getFragmentManager() instanceof android.support.v4.app.FragmentManager) {
            fragImpl = new SupportDialogFragmentImpl();
            frag = new OAuthDialogFragment((android.support.v4.app.DialogFragment) fragImpl,
                controller.fullScreen, controller.horizontalProgress, controller.hideFullScreenTitle);
            if (controller.hideFullScreenTitle) {
                if (SDK_INT >= ICE_CREAM_SANDWICH) {
                    ((android.support.v4.app.DialogFragment) fragImpl).setStyle(android.support
                            .v4.app.DialogFragment.STYLE_NORMAL,
                        android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen
                    );
                } else {
                    ((android.support.v4.app.DialogFragment) fragImpl).setStyle(android.support
                            .v4.app.DialogFragment.STYLE_NORMAL,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen
                    );
                }
            }
        } else {
            fragImpl = new NativeDialogFragmentImpl();
            frag = new OAuthDialogFragment((android.app.DialogFragment) fragImpl,
                controller.fullScreen, controller.horizontalProgress, controller.hideFullScreenTitle);
            if (controller.hideFullScreenTitle) {
                if (SDK_INT >= ICE_CREAM_SANDWICH) {
                    ((android.app.DialogFragment) fragImpl).setStyle(DialogFragment.STYLE_NORMAL,
                        android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                } else {
                    ((android.app.DialogFragment) fragImpl).setStyle(DialogFragment.STYLE_NORMAL,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                }
            }
        }
        fragImpl.setDialogFragmentCompat(frag);
        frag.setArguments(args);
        frag.setController(controller);
        return frag;
    }

    @Override
    Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mController.onPrepareDialog(dialog);
        return dialog;
    }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View customLayout = mController.onCreateView(inflater, container,
                savedInstanceState);
        if (customLayout != null) {
            return customLayout;
        }

        final Context context = inflater.getContext();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int DIALOG_HEIGHT = (int) Math.min(0.8f * metrics.heightPixels, 1024);

        FrameLayout root = new FrameLayout(context);
        root.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        WebView wv = new WebView(context);
        wv.setId(android.R.id.primary);

        if (mFullScreen) {
            root.addView(wv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        } else {
            root.addView(wv, new LayoutParams(LayoutParams.FILL_PARENT, DIALOG_HEIGHT));
        }

        if (mHorizontalProgress && !mHideFullScreenTitle) {
            RelativeLayout pframe = new RelativeLayout(context);
            pframe.setId(android.R.id.widget_frame);
            pframe.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.topMargin = -20;
            ProgressBar progress =
                new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            progress.setIndeterminate(true);
            progress.setId(android.R.id.progress);
            pframe.addView(progress, params);
            root.addView(pframe,
                new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        } else {
            LinearLayout pframe = new LinearLayout(context);
            pframe.setId(android.R.id.widget_frame);
            pframe.setOrientation(LinearLayout.VERTICAL);
            pframe.setVisibility(View.GONE);
            pframe.setGravity(Gravity.CENTER);
            ProgressBar progress =
                new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
            progress.setId(android.R.id.progress);
            pframe.addView(progress,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            TextView progressText = new TextView(context, null, android.R.attr.textViewStyle);
            progressText.setId(android.R.id.text1);
            pframe.addView(progressText,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if (mFullScreen && mHideFullScreenTitle) {
                root.addView(pframe,
                    new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            } else {
                root.addView(pframe,
                    new LayoutParams(LayoutParams.FILL_PARENT, DIALOG_HEIGHT));
            }
        }

        return root;
    }

    static boolean isRedirectUriFound(String uri, String redirectUri) {
        Uri u = null;
        Uri r = null;
        try {
            u = Uri.parse(uri);
            r = Uri.parse(redirectUri);
        } catch (NullPointerException e) {
            return false;
        }
        if (u == null || r == null) {
            return false;
        }
        boolean rOpaque = r.isOpaque();
        boolean uOpaque = u.isOpaque();
        if (rOpaque != uOpaque) {
            return false;
        }
        if (rOpaque) {
            return TextUtils.equals(uri, redirectUri);
        }
        if (!TextUtils.equals(r.getScheme(), u.getScheme())) {
            return false;
        }
        if (!TextUtils.equals(r.getAuthority(), u.getAuthority())) {
            return false;
        }
        if (r.getPort() != u.getPort()) {
            return false;
        }
        if (!TextUtils.isEmpty(r.getPath()) && !TextUtils.equals(r.getPath(), u.getPath())) {
            return false;
        }
        Set<String> paramKeys = CompatUri.getQueryParameterNames(r);
        for (String key : paramKeys) {
            if (!TextUtils.equals(r.getQueryParameter(key), u.getQueryParameter(key))) {
                return false;
            }
        }
        String frag = r.getFragment();
        if (!TextUtils.isEmpty(frag)
                && !TextUtils.equals(frag, u.getFragment())) {
            return false;
        }
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    void onViewCreated(View view, Bundle savedInstanceState) {
        View rawWebView = view.findViewById(android.R.id.primary);
        if (rawWebView == null) {
            throw new RuntimeException(
                    "Your content must have a WebView whose id attribute is " +
                            "'android.R.id.primary'");
        }
        if (!(rawWebView instanceof WebView)) {
            throw new RuntimeException(
                    "Content has view with id attribute 'android.R.id.primary' "
                            + "that is not a WebView class");
        }
        WebView wv = (WebView) rawWebView;
        WebSettings webSettings = wv.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        wv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                WebView wv = (WebView) v;
                if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
                    wv.goBack();
                    return true;
                }
                return false;
            }
        });

        if (mController.isJavascriptEnabledForWebView()) {
            webSettings.setJavaScriptEnabled(true);
        }

        if (mController.disableWebViewCache()) {
            webSettings.setAppCacheEnabled(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        if (mController.removePreviousCookie()) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }

        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 0 && newProgress != 100) {
                    setProgressShown(view.getUrl(), getView(), newProgress);
                }
            }

        });

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LOGGER.info("shouldOverrideUrlLoading: " + url);
                interceptUrlCompat(view, url, true);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LOGGER.info("onPageStarted: " + url);
                if (!interceptUrlCompat(view, url, false)) {
                    setProgressShown(url, getView(), 0);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setProgressShown(url, getView(), 100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                onError(description);
            }

            private boolean interceptUrlCompat(WebView view, String url, boolean loadUrl) {
                if (!isAdded() || isRemoving() || mController == null) {
                    return false;
                }
                String redirectUri = null;
                try {
                    redirectUri = mController.getRedirectUri();
                } catch (IOException e) {
                    onError(e.getMessage());
                    return false;
                }

                String authorizationType = getArguments().getString(ARG_AUTHORIZATION_TYPE);
                LOGGER.info("url: " + url + ", redirect: " + redirectUri + ", callback: "
                        + isRedirectUriFound(url, redirectUri));
                if (isRedirectUriFound(url, redirectUri)) {
                    if (TextUtils.equals(authorizationType, AUTHORIZATION_10A)) {
                        OAuth10aResponseUrl responseUrl = new OAuth10aResponseUrl(url);
                        mController.set(responseUrl.getVerifier(), responseUrl.getError(), null,
                                true);
                    } else if (TextUtils.equals(authorizationType, AUTHORIZATION_EXPLICIT)) {
                        AuthorizationCodeResponseUrl responseUrl =
                                new AuthorizationCodeResponseUrl(url);
                        String error = responseUrl.getError();
                        if (!TextUtils.isEmpty(error)
                                && !TextUtils.isEmpty(responseUrl.getErrorDescription())) {
                            error += (": " + responseUrl.getErrorDescription());
                        }
                        mController.set(responseUrl.getCode(), error, null, true);
                    } else { // implicit
                        ImplicitResponseUrl implicitResponseUrl = new ImplicitResponseUrl(url);
                        String error = implicitResponseUrl.getError();
                        if (!TextUtils.isEmpty(error)
                                && !TextUtils.isEmpty(implicitResponseUrl.getErrorDescription())) {
                            error += (": " + implicitResponseUrl.getErrorDescription());
                        }
                        mController.set(implicitResponseUrl.getAccessToken(), error,
                                implicitResponseUrl, true);
                    }
                    return true;
                }
                if (loadUrl) {
                    view.loadUrl(url);
                }
                return false;
            }

        });
    }

    @Override
    void onActivityCreated(Bundle savedInstanceState) {
        WebView wv = (WebView) getView().findViewById(android.R.id.primary);
        if (wv != null) {
            wv.loadUrl(getArguments().getString(ARG_AUTHORIZATION_REQUEST_URL));
        }
        if (mFullScreen) {
            getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(null);
        }
        if (mHorizontalProgress) {
            View divider = getDialog().findViewById(getDialog().getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null));
            if (divider != null) {
                divider.setBackgroundColor(getDialog().getContext().getResources().getColor(
                    android.R.color.background_dark));
            }
        }
    }

    @Override
    void onDestroy() {
        setController(null);
    }

    @Override
    void onCancel(DialogInterface dialog) {
        onError(AuthorizationUIController.ERROR_USER_CANCELLED);
    }

    private void onError(String errorMessage) {
        if (mController != null) {
            mController.set(null, errorMessage, null, true);
        }
    }

    private void setProgressShown(String url, View view, int newProgress) {
        boolean handled = false;
        if (mController != null) {
            handled = mController.setProgressShown(url, view, newProgress);
        }
        View progress = null;
        View progressbar = null;
        if (!handled) {
            if (view != null) {
                progress = view.findViewById(android.R.id.text1);
                progressbar = view.findViewById(android.R.id.progress);
                view = view.findViewById(android.R.id.widget_frame);
            }
            if (view != null) {
                if (progress != null && progress instanceof TextView) {
                    ((TextView) progress).setText(newProgress + "%");
                }
                if (progressbar != null && progressbar instanceof ProgressBar) {
                    if (newProgress > 0 && newProgress < 100) {
                        ((ProgressBar) progressbar).setIndeterminate(false);
                    }
                    ((ProgressBar) progressbar).setProgress(newProgress);
                }
                view.setVisibility(newProgress != 100 ? View.VISIBLE : View.GONE);
            }
        }
    }

}
