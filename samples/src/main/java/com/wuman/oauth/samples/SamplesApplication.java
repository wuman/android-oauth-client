
package com.wuman.oauth.samples;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class SamplesApplication extends Application {

    // Volley
    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        cancelAllRequests();
    }

    @Override
    public void onTerminate() {
        cancelAllRequests();
        super.onTerminate();
    }

    void cancelAllRequests() {
        mRequestQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    private static final class BitmapCache implements ImageLoader.ImageCache {

        private static final long MEM_CACHE_SIZE =
                Math.min(Runtime.getRuntime().maxMemory() / 4, 16 * 1024 * 1024);
        private LruCache<String, Bitmap> bitmapCache;

        BitmapCache() {
            bitmapCache = new LruCache<String, Bitmap>((int) MEM_CACHE_SIZE);
        }

        private static String urlToKey(String url) {
            return Integer.toString(Math.abs(url.hashCode()));
        }

        @Override
        public Bitmap getBitmap(String url) {
            return bitmapCache.get(urlToKey(url));
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            bitmapCache.put(urlToKey(url), bitmap);
        }

    }

}
