
package com.wuman.oauth.samples;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Lists;
import com.wuman.android.auth.AuthorizationDialogController;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.foursquare.FoursquareConstants;

import java.io.IOException;
import java.util.logging.Logger;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomizedProgressActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            OAuthFragment list = new OAuthFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class OAuthFragment extends Fragment implements
            LoaderManager.LoaderCallbacks<Result<Credential>> {

        private static final int LOADER_GET_TOKEN = 0;
        private static final int LOADER_DELETE_TOKEN = 1;

        private OAuthManager oauth;

        private Button button;
        private TextView message;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.delete_cookies_menu, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_cookies: {
                    CookieSyncManager.createInstance(getActivity());
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookie();
                    return true;
                }
                default: {
                    return super.onOptionsItemSelected(item);
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.oauth_login, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            button = (Button) view.findViewById(android.R.id.button1);
            setButtonText(R.string.get_token);
            message = (TextView) view.findViewById(android.R.id.text1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().equals(R.string.get_token)) {
                        if (getLoaderManager().getLoader(LOADER_GET_TOKEN) == null) {
                            getLoaderManager().initLoader(LOADER_GET_TOKEN, null,
                                    OAuthFragment.this);
                        } else {
                            getLoaderManager().restartLoader(LOADER_GET_TOKEN, null,
                                    OAuthFragment.this);
                        }
                    } else { // R.string.delete_token
                        if (getLoaderManager().getLoader(LOADER_DELETE_TOKEN) == null) {
                            getLoaderManager().initLoader(LOADER_DELETE_TOKEN, null,
                                    OAuthFragment.this);
                        } else {
                            getLoaderManager().restartLoader(LOADER_DELETE_TOKEN, null,
                                    OAuthFragment.this);
                        }
                    }
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            boolean fullScreen = getActivity().getSharedPreferences("Preference", 0)
                .getBoolean(SamplesActivity.KEY_AUTH_MODE, false);
            // setup credential store
            SharedPreferencesCredentialStore credentialStore =
                    new SharedPreferencesCredentialStore(getActivity(),
                            SamplesConstants.CREDENTIALS_STORE_PREF_FILE, OAuth.JSON_FACTORY);
            // setup authorization flow
            AuthorizationFlow flow = new AuthorizationFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    OAuth.HTTP_TRANSPORT,
                    OAuth.JSON_FACTORY,
                    new GenericUrl(FoursquareConstants.TOKEN_SERVER_URL),
                    new ClientParametersAuthentication(FoursquareConstants.CLIENT_ID, null),
                    FoursquareConstants.CLIENT_ID,
                    FoursquareConstants.AUTHORIZATION_IMPLICIT_SERVER_URL)
                    .setScopes(Lists.<String> newArrayList())
                    .setCredentialStore(credentialStore)
                    .build();
            // setup UI controller with customized layout
            AuthorizationDialogController controller =
                    new CustomController(getFragmentManager(), fullScreen);
            // instantiate an OAuthManager instance
            oauth = new OAuthManager(flow, controller);
        }

        @Override
        public Loader<Result<Credential>> onCreateLoader(int id, Bundle args) {
            getActivity().setProgressBarIndeterminateVisibility(true);
            button.setEnabled(false);
            message.setText("");
            if (id == LOADER_GET_TOKEN) {
                return new GetTokenLoader(getActivity(), oauth);
            } else {
                return new DeleteTokenLoader(getActivity(), oauth);
            }
        }

        @Override
        public void onLoadFinished(Loader<Result<Credential>> loader,
                Result<Credential> result) {
            if (loader.getId() == LOADER_GET_TOKEN) {
                message.setText(result.success ? result.data.getAccessToken() : "");
            } else {
                message.setText("");
            }
            if (result.success) {
                if (loader.getId() == LOADER_GET_TOKEN) {
                    setButtonText(R.string.delete_token);
                } else {
                    setButtonText(R.string.get_token);
                }
            } else {
                setButtonText(R.string.get_token);
                Crouton.makeText(getActivity(), result.errorMessage, Style.ALERT).show();
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
            button.setEnabled(true);
        }

        @Override
        public void onLoaderReset(Loader<Result<Credential>> loader) {
            message.setText("");
            getActivity().setProgressBarIndeterminateVisibility(false);
            button.setEnabled(true);
        }

        @Override
        public void onDestroy() {
            getLoaderManager().destroyLoader(LOADER_GET_TOKEN);
            getLoaderManager().destroyLoader(LOADER_DELETE_TOKEN);
            super.onDestroy();
        }

        private void setButtonText(int action) {
            button.setText(action);
            button.setTag(action);
        }

        private static class GetTokenLoader extends AsyncResourceLoader<Credential> {

            private final OAuthManager oauth;

            public GetTokenLoader(Context context, OAuthManager oauth) {
                super(context);
                this.oauth = oauth;
            }

            @Override
            public Credential loadResourceInBackground() throws Exception {
                Credential credential =
                        oauth.authorizeImplicitly(getContext().getString(R.string.token_customui), null, null)
                                .getResult();
                LOGGER.info("token: " + credential.getAccessToken());
                return credential;
            }

            @Override
            public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<Credential> result) {
                Credential data = result.data;
                result.success = !TextUtils.isEmpty(data.getAccessToken());
                result.errorMessage = result.success ? null : "error";
            }

        }

        private static class DeleteTokenLoader extends AsyncResourceLoader<Credential> {

            private final OAuthManager oauth;

            private boolean success;

            public DeleteTokenLoader(Context context, OAuthManager oauth) {
                super(context);
                this.oauth = oauth;
            }

            @Override
            public Credential loadResourceInBackground() throws Exception {
                success = oauth.deleteCredential(getContext().getString(R.string.token_customui), null, null)
                        .getResult();
                LOGGER.info("token deleted: " + success);
                return null;
            }

            @Override
            public void updateErrorStateIfApplicable(Result<Credential> result) {
                result.success = success;
                result.errorMessage = result.success ? null : "error";
            }

        }

    }

    private static final class CustomController extends DialogFragmentController {

        public CustomController(FragmentManager fragmentManager, boolean fullScreen) {
            super(fragmentManager, fullScreen);
        }

        @Override
        public void onPrepareDialog(Dialog dialog) {
            super.onPrepareDialog(dialog);
            dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
            dialog.setTitle(dialog.getContext().getString(R.string.get_token));
            dialog.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Dialog d = (Dialog) dialog;
                    d.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                            android.R.drawable.ic_dialog_info);
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            Context context = inflater.getContext();
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            final int DIALOG_HEIGHT =
                    (int) Math.min(0.8f * metrics.heightPixels, 1024);

            View root = inflater.inflate(R.layout.custom_ui, container, false);

            WebView wv = (WebView) root.findViewById(android.R.id.primary);
            LayoutParams params = wv.getLayoutParams();
            params.height = DIALOG_HEIGHT;
            wv.setLayoutParams(params);
            View progressContainer = root.findViewById(android.R.id.progress);
            params = progressContainer.getLayoutParams();
            params.height = DIALOG_HEIGHT;
            progressContainer.setLayoutParams(params);

            return root;
        }

        @Override
        public boolean setProgressShown(String url, View view, int newProgress) {
            TextView urlView = (TextView) view.findViewById(android.R.id.text2);
            urlView.setText(url);
            final boolean hasText = !TextUtils.isEmpty(url);
            urlView.setVisibility(hasText ? View.VISIBLE : View.GONE);
            return false;
        }

        @Override
        public String getRedirectUri() throws IOException {
            return FoursquareConstants.REDIRECT_URL;
        }

        @Override
        public boolean isJavascriptEnabledForWebView() {
            return true;
        }

        @Override
        public boolean disableWebViewCache() {
            return false;
        }

        @Override
        public boolean removePreviousCookie() {
            return false;
        }

    }

}
