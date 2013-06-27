
package com.wuman.oauth.samples.ui;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.google.api.client.util.Preconditions;
import com.wuman.oauth.samples.R;

public class ContentDecoratorAdapter implements WrapperListAdapter {

    private final Loadable<?> mLoadable;
    private final ListAdapter mAdapter;

    public ContentDecoratorAdapter(Loadable<?> loadable, ListAdapter adapter) {
        super();
        this.mLoadable = Preconditions.checkNotNull(loadable);
        this.mAdapter = Preconditions.checkNotNull(adapter);
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    private boolean isItem(int position) {
        return position < mAdapter.getCount();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private boolean hasError() {
        return mLoadable.hasError();
    }

    private boolean hasMore() {
        return mLoadable.hasMore();
    }

    @Override
    public int getCount() {
        int count = mAdapter.getCount();
        if (count != 0) {
            if (hasError() || hasMore()) {
                count++;
            }
        } else {
            // Don't show footers when the list is empty
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (isItem(position)) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (isItem(position)) {
            return mAdapter.getItemId(position);
        } else {
            return AdapterView.INVALID_ROW_ID;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isItem(position)) {
            return mAdapter.getItemViewType(position);
        } else {
            return AdapterView.ITEM_VIEW_TYPE_IGNORE;
        }
    }

    private static LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isItem(position)) {
            return mAdapter.getView(position, convertView, parent);
        } else if (hasError()) {
            return newErrorView(parent);
        } else if (hasMore()) {
            return newLoadingView(parent);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private static View newLoadingView(ViewGroup parent) {
        return getLayoutInflater(parent).inflate(R.layout.footer_loading, parent, false);
    }

    private static View newErrorView(ViewGroup parent) {
        return getLayoutInflater(parent).inflate(R.layout.footer_error, parent, false);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public final boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

}
