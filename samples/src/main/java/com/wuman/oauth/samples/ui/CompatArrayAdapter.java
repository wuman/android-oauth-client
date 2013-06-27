
package com.wuman.oauth.samples.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CompatArrayAdapter<T> extends ArrayAdapter<T> {

    public CompatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CompatArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CompatArrayAdapter(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, objects);
    }

    public CompatArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    public CompatArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CompatArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void compatAddAll(Collection<T> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addAll(collection);
        } else {
            setNotifyOnChange(false);
            for (Iterator<T> iter = collection.iterator(); iter.hasNext();) {
                T item = iter.next();
                if (!iter.hasNext()) {
                    setNotifyOnChange(true);
                }
                add(item);
            }
            setNotifyOnChange(true);
        }
    }

}
