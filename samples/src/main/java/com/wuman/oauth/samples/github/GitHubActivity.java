
package com.wuman.oauth.samples.github;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.util.Lists;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.R;
import com.wuman.oauth.samples.SamplesConstants;
import com.wuman.oauth.samples.github.api.GitHub;
import com.wuman.oauth.samples.github.api.GitHub.User.ListReposRequest;
import com.wuman.oauth.samples.github.api.GitHubRequestInitializer;
import com.wuman.oauth.samples.github.api.model.Repositories;
import com.wuman.oauth.samples.github.api.model.Repository;
import com.wuman.oauth.samples.ui.CompatArrayAdapter;
import com.wuman.oauth.samples.ui.ContentDecoratorAdapter;
import com.wuman.oauth.samples.ui.ListScrollListener;
import com.wuman.oauth.samples.ui.Loadable;
import com.wuman.oauth.samples.ui.LoadableDecorator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.List;
import java.util.logging.Logger;

public class GitHubActivity extends FragmentActivity {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            GitHubListFragment list = new GitHubListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    public static class RepositoriesAdapter extends CompatArrayAdapter<Repository> {

        private final LayoutInflater mInflater;

        public RepositoriesAdapter(Context context) {
            super(context, R.layout.simple_list_item_text_with_image);
            mInflater = LayoutInflater.from(context);
        }

        public void setData(Repositories repositories, boolean clear) {
            if (clear) {
                clear();
            }
            if (repositories != null) {
                List<Repository> repos = repositories.getRepositories();
                if (repos != null) {
                    compatAddAll(repos);
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

            Repository repository = getItem(position);
            String repoName = repository.getFullName();
            SpannableStringBuilder builder =
                    new SpannableStringBuilder(repoName + "\n" + repository.getDescription());
            builder.setSpan(new TextAppearanceSpan(getContext(),
                    android.R.style.TextAppearance_Large), 0, repoName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder, BufferType.SPANNABLE);
            imageView.setVisibility(View.GONE);

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

    public static class GitHubLoader extends AsyncResourceLoader<Repositories> {

        private final OAuth oauth;
        private ListReposRequest request;
        private final int page;

        public GitHubLoader(FragmentActivity activity, int since) {
            super(activity);
            this.page = since;
            this.oauth = OAuth.newInstance(activity.getApplicationContext(),
                    activity.getSupportFragmentManager(),
                    new ClientParametersAuthentication(GitHubConstants.CLIENT_ID,
                            GitHubConstants.CLIENT_SECRET),
                    GitHubConstants.AUTHORIZATION_CODE_SERVER_URL,
                    GitHubConstants.TOKEN_SERVER_URL,
                    GitHubConstants.REDIRECT_URL,
                    Lists.<String> newArrayList());
        }

        public boolean isLoadMoreRequest() {
            return page != 0;
        }

        @Override
        public Repositories loadResourceInBackground() throws Exception {
            Credential credential = oauth.authorizeExplicitly(
                    getContext().getString(R.string.token_github)).getResult();
            LOGGER.info("token: " + credential.getAccessToken());

            GitHub github =
                    new GitHub.Builder(OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, credential)
                            .setApplicationName(getContext().getString(R.string.app_name_long))
                            .setGitHubRequestInitializer(new GitHubRequestInitializer(credential))
                            .build();
            request = github.user().repos();
            if (isLoadMoreRequest()) {
                request.setPage(page);
            }
            Repositories repositories = request.execute();
            return repositories;
        }

        @Override
        public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<Repositories> result) {
            Repositories data = result.data;
            result.success = request.getLastStatusCode() == HttpStatusCodes.STATUS_CODE_OK;
            if (result.success) {
                result.errorMessage = null;
            } else {
                result.errorMessage = data.getMessage();
                if (data.getErrors() != null && data.getErrors().size() > 0) {
                    com.wuman.oauth.samples.github.api.model.Error error = data.getErrors().get(0);
                    result.errorMessage += (error.getCode());
                }
            }
        }

    }

    public static class GitHubListFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Result<Repositories>> {

        RepositoriesAdapter mAdapter;
        Loadable<Repositories> mLoadable;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            mAdapter = new RepositoriesAdapter(getActivity().getApplicationContext());
            mLoadable = new RepositoriesLoadable(getLoaderManager(), 0,
                    new LoadableDecorator<Repositories>(this, 0, this));
            setListAdapter(new ContentDecoratorAdapter(mLoadable, mAdapter));
            getListView().setOnScrollListener(new ListScrollListener(mLoadable));

            mLoadable.init();
        }

        @Override
        public Loader<Result<Repositories>> onCreateLoader(int id, Bundle args) {
            return new GitHubLoader(getActivity(), RepositoriesLoadable.getPage(args));
        }

        @Override
        public void onLoadFinished(Loader<Result<Repositories>> loader, Result<Repositories> result) {
            final boolean clear = !((GitHubLoader) loader).isLoadMoreRequest();
            mAdapter.setData(result.data, clear);
        }

        @Override
        public void onLoaderReset(Loader<Result<Repositories>> loader) {
            mAdapter.setData(null, true);
        }

        @Override
        public void onDestroy() {
            mLoadable.destroy();
            super.onDestroy();
        }

    }

}
