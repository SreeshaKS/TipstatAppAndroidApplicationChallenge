package com.hiringchallenge.hackerearth.tipstatapp.Network;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private Context context;

    public VolleySingleton(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }
    public static VolleySingleton getsInstance(Context context) {

        if (sInstance == null) {
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
