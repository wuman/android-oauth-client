
package com.wuman.oauth.samples.instagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpStatusCodes;
import com.squareup.picasso.Picasso;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.R;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.instagram.api.Instagram;
import com.wuman.oauth.samples.instagram.api.Instagram.Users.Self.FeedRequest;
import com.wuman.oauth.samples.instagram.api.InstagramRequestInitializer;
import com.wuman.oauth.samples.instagram.api.InstagramScopes;
import com.wuman.oauth.samples.instagram.api.model.Feed;
import com.wuman.oauth.samples.instagram.api.model.FeedItem;
import com.wuman.oauth.samples.instagram.api.model.Meta;
import com.wuman.oauth.samples.ui.CompatArrayAdapter;
import com.wuman.oauth.samples.ui.ContentDecoratorAdapter;
import com.wuman.oauth.samples.ui.ListScrollListener;
import com.wuman.oauth.samples.ui.Loadable;
import com.wuman.oauth.samples.ui.LoadableDecorator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class InstagramActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            InstagramListFragment list = new InstagramListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class FeedAdapter extends CompatArrayAdapter<FeedItem> {

        private final LayoutInflater mInflater;

        public FeedAdapter(Context context) {
            super(context, R.layout.simple_list_item_image);
            mInflater = LayoutInflater.from(context);
        }

        public void setData(Feed feed, boolean clear) {
            if (clear) {
                clear();
            }
            if (feed != null) {
                List<FeedItem> feedItems = feed.getData();
                if (feedItems != null) {
                    compatAddAll(feedItems);
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater
                        .inflate(R.layout.simple_list_item_image, parent, false);
                ViewHolder holder = new ViewHolder(
                        (ImageView) view.findViewById(android.R.id.icon1),
                        (ImageView) view.findViewById(android.R.id.icon2),
                        (TextView) view.findViewById(android.R.id.text1));
                view.setTag(holder);
            } else {
                view = convertView;
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            FeedItem item = getItem(position);
            String imageUrl = item.getImages().getThumbnail().getUrl();
            Picasso.with(getContext()).load(imageUrl).into(holder.imageView);
            String avatarUrl = item.getUser().getProfilePicture();
            Picasso.with(getContext()).load(avatarUrl).into(holder.avatarView);
            holder.usernameView.setText(item.getUser().getFullName());

            return view;
        }

        private static final class ViewHolder {

            ViewHolder(ImageView imageView, ImageView avatarView,
                    TextView usernameView) {
                super();
                this.imageView = imageView;
                this.avatarView = avatarView;
                this.usernameView = usernameView;
            }

            ImageView imageView;
            ImageView avatarView;
            TextView usernameView;
        }

    }

    public static class InstagramLoader extends AsyncResourceLoader<Feed> {

        private final String nextMaxId;
        private final OAuth oauth;

        public InstagramLoader(FragmentActivity activity, String nextMaxId) {
            super(activity);
            this.nextMaxId = nextMaxId;
            oauth = OAuth.newInstance(activity.getApplicationContext(),
                    activity.getSupportFragmentManager(),
                    new ClientParametersAuthentication(InstagramConstants.CLIENT_ID, null),
                    InstagramConstants.AUTHORIZATION_IMPLICIT_SERVER_URL,
                    InstagramConstants.TOKEN_SERVER_URL,
                    InstagramConstants.REDIRECT_URL,
                    Arrays.asList(InstagramScopes.BASIC, InstagramScopes.COMMENTS,
                            InstagramScopes.LIKES, InstagramScopes.RELATIONSHIPS));
        }

        public boolean isLoadMoreRequest() {
            return !TextUtils.isEmpty(nextMaxId);
        }

        @Override
        public Feed loadResourceInBackground() throws Exception {
            Credential credential = oauth.authorizeImplicitly(
                    getContext().getString(R.string.token_instagram)).getResult();
            LOGGER.info("token: " + credential.getAccessToken());

            Instagram instagram =
                    new Instagram.Builder(OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, null)
                            .setApplicationName(getContext().getString(R.string.app_name_long))
                            .setInstagramRequestInitializer(
                                    new InstagramRequestInitializer(credential))
                            .build();
            FeedRequest request = instagram.users().self().feed();
            if (isLoadMoreRequest()) {
                request.setMaxId(nextMaxId);
            }
            Feed feed = request.execute();
            return feed;
        }

        @Override
        public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<Feed> result) {
            Feed data = result.data;
            Meta meta = data.getMeta();
            result.success = meta.getCode() == HttpStatusCodes.STATUS_CODE_OK;
            result.errorMessage = result.success ? null :
                    (meta.getCode() + " " + meta.getErrorType() + ": " + meta.getErrorMessage());
        }

    }

    public static class InstagramListFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Result<Feed>> {

        FeedAdapter mAdapter;
        Loadable<Feed> mLoadable;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            mAdapter = new FeedAdapter(getActivity().getApplicationContext());
            mLoadable = new FeedLoadable(getLoaderManager(), 0,
                    new LoadableDecorator<Feed>(this, 0, this));
            setListAdapter(new ContentDecoratorAdapter(mLoadable, mAdapter));
            getListView().setOnScrollListener(new ListScrollListener(mLoadable));

            mLoadable.init();
        }

        @Override
        public Loader<Result<Feed>> onCreateLoader(int id, Bundle args) {
            return new InstagramLoader(getActivity(), FeedLoadable.getMaxId(args));
        }

        @Override
        public void onLoadFinished(Loader<Result<Feed>> loader, Result<Feed> result) {
            final boolean clear = !((InstagramLoader) loader).isLoadMoreRequest();
            mAdapter.setData(result.data, clear);
        }

        @Override
        public void onLoaderReset(Loader<Result<Feed>> loader) {
            mAdapter.setData(null, true);
        }

        @Override
        public void onDestroy() {
            mLoadable.destroy();
            super.onDestroy();
        }

    }

}
