
package com.wuman.oauth.samples.plurk;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.squareup.picasso.Picasso;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.R;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.plurk.api.Plurk.Timeline.GetPlurksRequest;
import com.wuman.oauth.samples.plurk.api.PlurkRequestInitializer;
import com.wuman.oauth.samples.plurk.api.model.Plurk;
import com.wuman.oauth.samples.plurk.api.model.Timeline;
import com.wuman.oauth.samples.plurk.api.model.User;
import com.wuman.oauth.samples.ui.CompatArrayAdapter;
import com.wuman.oauth.samples.ui.ContentDecoratorAdapter;
import com.wuman.oauth.samples.ui.ListScrollListener;
import com.wuman.oauth.samples.ui.Loadable;
import com.wuman.oauth.samples.ui.LoadableDecorator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.List;
import java.util.logging.Logger;

public class PlurkActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            PlurkListFragment list = new PlurkListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class TimelineAdapter extends CompatArrayAdapter<Plurk> {

        private final LayoutInflater mInflater;

        public TimelineAdapter(Context context) {
            super(context, R.layout.simple_list_item_text_with_image);
            mInflater = LayoutInflater.from(context);
        }

        public void setData(Timeline timeline, boolean clear) {
            if (clear) {
                clear();
            }
            if (timeline != null) {
                List<Plurk> plurks = timeline.getPlurks();
                if (plurks != null) {
                    for (Plurk plurk : plurks) {
                        User owner = timeline.getUsers().get(Long.toString(plurk.getOwnerId()));
                        if (owner != null) {
                            plurk.setOwner(owner);
                        }
                    }
                    compatAddAll(plurks);
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater
                        .inflate(R.layout.simple_list_item_text_with_image, parent, false);
                ViewHolder holder = new ViewHolder(
                        (TextView) view.findViewById(android.R.id.text1),
                        (ImageView) view.findViewById(android.R.id.icon));
                view.setTag(holder);
            } else {
                view = convertView;
            }

            ViewHolder holder = (ViewHolder) view.getTag();
            TextView textView = holder.textView;
            ImageView imageView = holder.imageView;

            Plurk plurk = getItem(position);
            String username = plurk.getOwner().getDisplayName();
            if (TextUtils.isEmpty(username)) {
                username = plurk.getOwner().getNickName();
            }
            SpannableStringBuilder builder =
                    new SpannableStringBuilder(username + "\n" + plurk.getContent());
            builder.setSpan(new TextAppearanceSpan(getContext(),
                    android.R.style.TextAppearance_Large), 0, username.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder, BufferType.SPANNABLE);
            Picasso.with(getContext()).load(plurk.getOwner().getProfileImage()).into(imageView);

            return view;
        }

        private static final class ViewHolder {

            ViewHolder(TextView textView, ImageView imageView) {
                super();
                this.textView = textView;
                this.imageView = imageView;
            }

            TextView textView;
            ImageView imageView;
        }

    }

    public static class PlurkLoader extends AsyncResourceLoader<Timeline> {

        private final DateTime offset;
        private final OAuth oauth;

        public PlurkLoader(FragmentActivity activity, DateTime offset) {
            super(activity);
            this.offset = offset;
            oauth = OAuth.newInstance(activity.getApplicationContext(),
                    activity.getSupportFragmentManager(),
                    new ClientParametersAuthentication(PlurkConstants.CONSUMER_KEY,
                            PlurkConstants.CONSUMER_SECRET),
                    PlurkConstants.AUTHORIZATION_VERIFIER_SERVER_URL,
                    PlurkConstants.TOKEN_SERVER_URL,
                    PlurkConstants.REDIRECT_URL,
                    Lists.<String> newArrayList(),
                    PlurkConstants.TEMPORARY_TOKEN_REQUEST_URL);
        }

        public boolean isLoadMoreRequest() {
            return offset != null;
        }

        @Override
        public Timeline loadResourceInBackground() throws Exception {
            Credential credential = oauth
                    .authorize10a(getContext().getString(R.string.token_plurk)).getResult();
            LOGGER.info("token: " + credential.getAccessToken());

            com.wuman.oauth.samples.plurk.api.Plurk plurk =
                    new com.wuman.oauth.samples.plurk.api.Plurk.Builder(
                            OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, credential)
                            .setApplicationName(getContext().getString(R.string.app_name_long))
                            .setPlurkRequestInitializer(new PlurkRequestInitializer())
                            .build();
            GetPlurksRequest request = plurk.timeline().getPlurks();
            request.setMinimalData(true);
            if (isLoadMoreRequest()) {
                request.setOffset(offset);
            }
            Timeline timeline = request.execute();
            return timeline;
        }

        @Override
        public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<Timeline> result) {
            Timeline data = result.data;
            String error = data.getErrorText();
            result.success = TextUtils.isEmpty(error);
            result.errorMessage = result.success ? null : error;
        }

    }

    public static class PlurkListFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Result<Timeline>> {

        TimelineAdapter mAdapter;
        Loadable<Timeline> mLoadable;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            mAdapter = new TimelineAdapter(getActivity().getApplicationContext());
            mLoadable = new PlurksLoadable(getLoaderManager(), 0,
                    new LoadableDecorator<Timeline>(this, 0, this));
            setListAdapter(new ContentDecoratorAdapter(mLoadable, mAdapter));
            getListView().setOnScrollListener(new ListScrollListener(mLoadable));

            mLoadable.init();
        }

        @Override
        public Loader<Result<Timeline>> onCreateLoader(int id, Bundle args) {
            return new PlurkLoader(getActivity(), PlurksLoadable.getOffset(args));
        }

        @Override
        public void onLoadFinished(Loader<Result<Timeline>> loader, Result<Timeline> result) {
            final boolean clear = !((PlurkLoader) loader).isLoadMoreRequest();
            mAdapter.setData(result.data, clear);
        }

        @Override
        public void onLoaderReset(Loader<Result<Timeline>> loader) {
            mAdapter.setData(null, true);
        }

        @Override
        public void onDestroy() {
            mLoadable.destroy();
            super.onDestroy();
        }

    }

}
