
package com.wuman.oauth.samples.flickr;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.google.api.client.util.Preconditions;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;
import com.wuman.oauth.samples.ui.Loadable;

public class PhotosLoadable implements Loadable<ContactsPhotos> {

    private final LoaderManager mLoaderManager;
    private final int mLoaderId;
    private final LoaderManager.LoaderCallbacks<Result<ContactsPhotos>> mCallbacks;

    private boolean mIsLoadingMore;
    private boolean mHasError;
    private boolean mHasMore;

    PhotosLoadable(LoaderManager loaderManager, int loaderId,
            LoaderCallbacks<Result<ContactsPhotos>> callbacks) {
        super();
        this.mLoaderManager = Preconditions.checkNotNull(loaderManager);
        this.mLoaderId = loaderId;
        this.mCallbacks = callbacks;
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
        mLoaderManager.restartLoader(mLoaderId, null, this);
    }

    @Override
    public void destroy() {
        mLoaderManager.destroyLoader(mLoaderId);
    }

    @Override
    public Loader<Result<ContactsPhotos>> onCreateLoader(int id, Bundle args) {
        return mCallbacks.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Result<ContactsPhotos>> loader, Result<ContactsPhotos> result) {
        mHasError = result == null || !result.success;
        mHasMore = false;
        mIsLoadingMore = false;
        mCallbacks.onLoadFinished(loader, result);
    }

    @Override
    public void onLoaderReset(Loader<Result<ContactsPhotos>> loader) {
        mHasError = false;
        mHasMore = false;
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
