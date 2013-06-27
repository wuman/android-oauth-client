
package com.wuman.oauth.samples.twitter;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.api.client.util.Preconditions;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.twitter.api.model.Timeline;
import com.wuman.oauth.samples.twitter.api.model.Tweet;
import com.wuman.oauth.samples.ui.Loadable;

import java.util.List;

public class TweetsLoadable implements Loadable<Timeline> {

    private static final String ARG_MAX_ID = "loadable:max-id";

    private final LoaderManager mLoaderManager;
    private final int mLoaderId;
    private final LoaderManager.LoaderCallbacks<Result<Timeline>> mCallbacks;

    private boolean mIsLoadingMore;
    private boolean mHasError;
    private boolean mHasMore;
    private String mNextMaxId;

    TweetsLoadable(LoaderManager loaderManager, int loaderId,
            LoaderCallbacks<Result<Timeline>> callbacks) {
        super();
        this.mLoaderManager = Preconditions.checkNotNull(loaderManager);
        this.mLoaderId = loaderId;
        this.mCallbacks = callbacks;
    }

    public static String getMaxId(Bundle args) {
        return args == null ? null : args.getString(ARG_MAX_ID);
    }

    @Override
    public void init() {
        mLoaderManager.initLoader(mLoaderId, Bundle.EMPTY, this);
    }

    @Override
    public boolean isReadyToLoadMore() {
        return mHasMore && !mHasError && !mIsLoadingMore;
    }

    @Override
    public void loadMore() {
        mIsLoadingMore = true;
        Bundle args = new Bundle();
        args.putString(ARG_MAX_ID, mNextMaxId);
        mLoaderManager.restartLoader(mLoaderId, args, this);
    }

    @Override
    public void destroy() {
        mLoaderManager.destroyLoader(mLoaderId);
    }

    @Override
    public Loader<Result<Timeline>> onCreateLoader(int id, Bundle args) {
        return mCallbacks.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Result<Timeline>> loader, Result<Timeline> result) {
        mHasError = result == null || !result.success;
        if (!mHasError) {
            List<Tweet> tweets = result.data.getTweets();
            if (tweets.size() > 0) {
                mNextMaxId = tweets.get(tweets.size() - 1).getId();
            } else {
                mNextMaxId = null;
            }
        } else {
            mNextMaxId = null;
        }
        mHasMore = !TextUtils.isEmpty(mNextMaxId);
        mIsLoadingMore = false;
        mCallbacks.onLoadFinished(loader, result);
    }

    @Override
    public void onLoaderReset(Loader<Result<Timeline>> loader) {
        mHasError = false;
        mHasMore = false;
        mNextMaxId = null;
        mIsLoadingMore = false;
        mCallbacks.onLoaderReset(loader);
    }

    @Override
    public boolean hasError() {
        return mHasError;
    }

    @Override
    public boolean hasMore() {
        return mHasMore;
    }

}
