
package com.wuman.oauth.samples.flickr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.Lists;
import com.squareup.picasso.Picasso;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.R;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.flickr.api.Flickr;
import com.wuman.oauth.samples.flickr.api.FlickrRequestInitializer;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;
import com.wuman.oauth.samples.flickr.api.model.Photo;
import com.wuman.oauth.samples.ui.CompatArrayAdapter;
import com.wuman.oauth.samples.ui.ContentDecoratorAdapter;
import com.wuman.oauth.samples.ui.ListScrollListener;
import com.wuman.oauth.samples.ui.Loadable;
import com.wuman.oauth.samples.ui.LoadableDecorator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.List;
import java.util.logging.Logger;

public class FlickrActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            FlickrListFragment list = new FlickrListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class PhotosAdapter extends CompatArrayAdapter<Photo> {

        private final LayoutInflater mInflater;

        public PhotosAdapter(Context context) {
            super(context, R.layout.simple_list_item_image);
            mInflater = LayoutInflater.from(context);
        }

        public void setData(ContactsPhotos photosContainer, boolean clear) {
            if (clear) {
                clear();
            }
            if (photosContainer != null) {
                List<Photo> photos = photosContainer.getPhotos().getPhotoList();
                if (photos != null) {
                    compatAddAll(photos);
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

            Photo photo = getItem(position);
            String imageUrl = FlickrConstants.generateSmallPhotoUrl(photo.getFarm(),
                    photo.getServer(),
                    photo.getId(), photo.getSecret());
            Picasso.with(getContext()).load(imageUrl).into(holder.imageView);
            String avatarUrl = FlickrConstants.generateBuddyIcon(photo.getIconFarm(),
                    photo.getIconServer(), photo.getOwner());
            Picasso.with(getContext()).load(avatarUrl).into(holder.avatarView);
            holder.usernameView.setText(photo.getOwnername());

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

    public static class FlickrLoader extends AsyncResourceLoader<ContactsPhotos> {

        private final OAuth oauth;

        public FlickrLoader(FragmentActivity activity) {
            super(activity);
            oauth = OAuth.newInstance(activity.getApplicationContext(),
                    activity.getSupportFragmentManager(),
                    new ClientParametersAuthentication(FlickrConstants.CONSUMER_KEY,
                            FlickrConstants.CONSUMER_SECRET),
                    FlickrConstants.AUTHORIZATION_VERIFIER_SERVER_URL,
                    FlickrConstants.TOKEN_SERVER_URL,
                    FlickrConstants.REDIRECT_URL,
                    Lists.<String> newArrayList(),
                    FlickrConstants.TEMPORARY_TOKEN_REQUEST_URL);
        }

        @Override
        public ContactsPhotos loadResourceInBackground() throws Exception {
            Credential credential = oauth.authorize10a(
                    getContext().getString(R.string.token_flickr)).getResult();
            LOGGER.info("token: " + credential.getAccessToken());

            Flickr flickr =
                    new Flickr.Builder(OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, credential)
                            .setApplicationName(getContext().getString(R.string.app_name_long))
                            .setFlickrRequestInitializer(new FlickrRequestInitializer())
                            .build();
            ContactsPhotos photos = flickr.photos().getContactsPhotos().setExtras("icon_server")
                    .execute();
            return photos;
        }

        @Override
        public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<ContactsPhotos> result) {
            ContactsPhotos data = result.data;
            result.success = "ok".equals(data.getStat());
            result.errorMessage = result.success ? null :
                    (data.getErrorCode() + ": " + data.getErrorMessage());
        }

    }

    public static class FlickrListFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Result<ContactsPhotos>> {

        PhotosAdapter mAdapter;
        Loadable<ContactsPhotos> mLoadable;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            mAdapter = new PhotosAdapter(getActivity().getApplicationContext());
            mLoadable = new PhotosLoadable(getLoaderManager(), 0,
                    new LoadableDecorator<ContactsPhotos>(this, 0, this));
            setListAdapter(new ContentDecoratorAdapter(mLoadable, mAdapter));
            getListView().setOnScrollListener(new ListScrollListener(mLoadable));

            mLoadable.init();
        }

        @Override
        public Loader<Result<ContactsPhotos>> onCreateLoader(int id, Bundle args) {
            return new FlickrLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Result<ContactsPhotos>> loader,
                Result<ContactsPhotos> result) {
            mAdapter.setData(result.data, true);
        }

        @Override
        public void onLoaderReset(Loader<Result<ContactsPhotos>> loader) {
            mAdapter.setData(null, true);
        }

        @Override
        public void onDestroy() {
            mLoadable.destroy();
            super.onDestroy();
        }

    }

}
