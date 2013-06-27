
package com.wuman.oauth.samples.github;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.api.client.util.Preconditions;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.github.api.model.Pagination;
import com.wuman.oauth.samples.github.api.model.Repositories;
import com.wuman.oauth.samples.ui.Loadable;

public class RepositoriesLoadable implements Loadable<Repositories> {

    private static final String ARG_PAGE = "loadable:page";

    private final LoaderManager mLoaderManager;
    private final int mLoaderId;
    private final LoaderManager.LoaderCallbacks<Result<Repositories>> mCallbacks;

    private boolean mIsLoadingMore;
    private boolean mHasError;
    private boolean mHasMore;
    private int mPage;

    RepositoriesLoadable(LoaderManager loaderManager, int loaderId,
            LoaderCallbacks<Result<Repositories>> callbacks) {
        super();
        this.mLoaderManager = Preconditions.checkNotNull(loaderManager);
        this.mLoaderId = loaderId;
        this.mCallbacks = callbacks;
    }

    public static int getPage(Bundle args) {
        return args == null ? null : args.getInt(ARG_PAGE);
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
        args.putInt(ARG_PAGE, mPage);
        mLoaderManager.restartLoader(mLoaderId, args, this);
    }

    @Override
    public void destroy() {
        mLoaderManager.destroyLoader(mLoaderId);
    }

    @Override
    public Loader<Result<Repositories>> onCreateLoader(int id, Bundle args) {
        return mCallbacks.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Result<Repositories>> loader, Result<Repositories> result) {
        mHasError = result == null || !result.success;
        if (!mHasError) {
            Pagination pagination = result.data.getPagination();
            mHasMore = (pagination != null && !TextUtils.isEmpty(pagination.getNext()));
        } else {
            mHasMore = false;
        }
        if (mHasMore) {
            Uri nextUri = Uri.parse(result.data.getPagination().getNext());
            mPage = Integer.parseInt(nextUri.getQueryParameter("page"));
        } else {
            mPage = 0;
        }
        mIsLoadingMore = false;
        mCallbacks.onLoadFinished(loader, result);
    }

    @Override
    public void onLoaderReset(Loader<Result<Repositories>> loader) {
        mHasError = false;
        mHasMore = false;
        mIsLoadingMore = false;
        mPage = 0;
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
