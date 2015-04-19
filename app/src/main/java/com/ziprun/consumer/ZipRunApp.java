package com.ziprun.consumer;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class ZipRunApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "SlgQ5jkFlMG5LEJafj996OQ9M";
    private static final String TWITTER_SECRET = "L2X0uZSxbBH6NGaSIJ8m1Cc1n9alCtq8XBZXWKtCqZ4pn08HcT";

    private static final String TAG = ZipRunApp.class.getCanonicalName();
    public static ZipRunApp APPLICATION;
    private static Context context;
    protected ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        APPLICATION = this;
        ZipRunApp.context = getApplicationContext();
        applicationGraph = ObjectGraph.create(getModules());
        applicationGraph.inject(this);
    }

    public static Context getAppContext() {
        return ZipRunApp.context;
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }

    public void inject(Object object) {
        applicationGraph.inject(object);
    }

    public Object getInstance(Class<?> cls) {
        return applicationGraph.get(cls);
    }

    protected Object[] getModules() {
        return Modules.list(this);
    }


    public final static class Constants {

        public static final LatLng DEFAULT_CAMERA_POSITION = new LatLng(28.586086, 77.171541);

        public static String API_KEY = "AIzaSyBc58zTmjGgsLR2N4RDkjiTN5HgBlwHUJo";
    }
}

