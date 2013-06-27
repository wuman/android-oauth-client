
package com.wuman.oauth.samples.ui;

import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.wuman.oauth.samples.AsyncResourceLoader.Result;

public interface Loadable<T> extends LoaderCallbacks<Result<T>> {

    boolean hasMore();

    boolean hasError();

    void init();

    void destroy();

    boolean isReadyToLoadMore();

    void loadMore();
}
