
package com.wuman.oauth.samples.twitter;

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
import com.google.api.client.util.Lists;
import com.squareup.picasso.Picasso;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.R;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.twitter.api.Twitter;
import com.wuman.oauth.samples.twitter.api.Twitter.Statuses.HomeTimelineRequest;
import com.wuman.oauth.samples.twitter.api.TwitterRequestInitializer;
import com.wuman.oauth.samples.twitter.api.model.Timeline;
import com.wuman.oauth.samples.twitter.api.model.Tweet;
import com.wuman.oauth.samples.ui.CompatArrayAdapter;
import com.wuman.oauth.samples.ui.ContentDecoratorAdapter;
import com.wuman.oauth.samples.ui.ListScrollListener;
import com.wuman.oauth.samples.ui.Loadable;
import com.wuman.oauth.samples.ui.LoadableDecorator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.List;
import java.util.logging.Logger;

public class TwitterActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            TwitterListFragment list = new TwitterListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class TimelineAdapter extends CompatArrayAdapter<Tweet> {

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
                List<Tweet> tweets = timeline.getTweets();
                if (tweets != null) {
                    compatAddAll(tweets);
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

            Tweet tweet = getItem(position);
            String username = tweet.getUser().getName();
            SpannableStringBuilder builder =
                    new SpannableStringBuilder(username + "\n" + tweet.getText());
            builder.setSpan(new TextAppearanceSpan(getContext(),
                    android.R.style.TextAppearance_Large), 0, username.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder, BufferType.SPANNABLE);
            Picasso.with(getContext()).load(tweet.getUser().getProfileImage()).into(imageView);

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

    public static class TwitterLoader extends AsyncResourceLoader<Timeline> {

        private final String nextMaxId;
        private final OAuth oauth;

        public TwitterLoader(FragmentActivity activity, String nextMaxId) {
            super(activity);
            this.nextMaxId = nextMaxId;
            oauth = OAuth.newInstance(activity.getApplicationContext(),
                    activity.getSupportFragmentManager(),
                    new ClientParametersAuthentication(TwitterConstants.CONSUMER_KEY,
                            TwitterConstants.CONSUMER_SECRET),
                    TwitterConstants.AUTHORIZATION_VERIFIER_SERVER_URL,
                    TwitterConstants.TOKEN_SERVER_URL,
                    TwitterConstants.REDIRECT_URL,
                    Lists.<String> newArrayList(),
                    TwitterConstants.TEMPORARY_TOKEN_REQUEST_URL);
        }

        public boolean isLoadMoreRequest() {
            return !TextUtils.isEmpty(nextMaxId);
        }

        @Override
        public Timeline loadResourceInBackground() throws Exception {
            Credential credential = oauth.authorize10a(
                    getContext().getString(R.string.token_twitter)).getResult();
            LOGGER.info("token: " + credential.getAccessToken());

            Twitter twitter =
                    new Twitter.Builder(OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, credential)
                            .setApplicationName(getContext().getString(R.string.app_name_long))
                            .setTwitterRequestInitializer(new TwitterRequestInitializer())
                            .build();
            HomeTimelineRequest request = twitter.statuses().homeTimelines();
            if (isLoadMoreRequest()) {
                request.setMaxId(nextMaxId);
            }
            Timeline timeline = request.execute();
            return timeline;
        }

        @Override
        public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<Timeline> result) {
            Timeline data = result.data;
            List<com.wuman.oauth.samples.twitter.api.model.Error> errors = data.getErrors();
            com.wuman.oauth.samples.twitter.api.model.Error error =
                    (errors != null && errors.size() > 0) ? errors.get(0) : null;
            result.success = error == null;
            result.errorMessage = result.success ? null :
                    (error.getCode() + ": " + error.getMessage());
        }

    }

    public static class TwitterListFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Result<Timeline>> {

        TimelineAdapter mAdapter;
        Loadable<Timeline> mLoadable;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            mAdapter = new TimelineAdapter(getActivity().getApplicationContext());
            mLoadable = new TweetsLoadable(getLoaderManager(), 0,
                    new LoadableDecorator<Timeline>(this, 0, this));
            setListAdapter(new ContentDecoratorAdapter(mLoadable, mAdapter));
            getListView().setOnScrollListener(new ListScrollListener(mLoadable));

            mLoadable.init();
        }

        @Override
        public Loader<Result<Timeline>> onCreateLoader(int id, Bundle args) {
            return new TwitterLoader(getActivity(), TweetsLoadable.getMaxId(args));
        }

        @Override
        public void onLoadFinished(Loader<Result<Timeline>> loader, Result<Timeline> result) {
            final boolean clear = !((TwitterLoader) loader).isLoadMoreRequest();
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
