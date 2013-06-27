
package com.wuman.oauth.samples.plurk;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Preconditions;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.plurk.api.model.Plurk;
import com.wuman.oauth.samples.plurk.api.model.Timeline;
import com.wuman.oauth.samples.ui.Loadable;

import java.util.List;

public class PlurksLoadable implements Loadable<Timeline> {

    private static final String ARG_OFFSET = "loadable:offset";

    private final LoaderManager mLoaderManager;
    private final int mLoaderId;
    private final LoaderManager.LoaderCallbacks<Result<Timeline>> mCallbacks;

    private boolean mIsLoadingMore;
    private boolean mHasError;
    private boolean mHasMore;
    private DateTime mOffset;

    PlurksLoadable(LoaderManager loaderManager, int loaderId,
            LoaderCallbacks<Result<Timeline>> callbacks) {
        super();
        this.mLoaderManager = Preconditions.checkNotNull(loaderManager);
        this.mLoaderId = loaderId;
        this.mCallbacks = callbacks;
    }

    public static DateTime getOffset(Bundle args) {
        return args == null || !args.containsKey(ARG_OFFSET) ? null :
                DateTime.parseRfc3339(args.getString(ARG_OFFSET));
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
        args.putString(ARG_OFFSET, mOffset.toStringRfc3339());
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
            List<Plurk> plurks = result.data.getPlurks();
            if (plurks.size() > 0) {
                mOffset = plurks.get(plurks.size() - 1).getPosted();
            } else {
                mOffset = null;
            }
        } else {
            mOffset = null;
        }
        mHasMore = mOffset != null;
        mIsLoadingMore = false;
        mCallbacks.onLoadFinished(loader, result);
    }

    @Override
    public void onLoaderReset(Loader<Result<Timeline>> loader) {
        mHasError = false;
        mHasMore = false;
        mOffset = null;
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
