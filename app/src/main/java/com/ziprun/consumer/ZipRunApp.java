    package com.ziprun.consumer;

    import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class ZipRunApp extends Application {

    private static final String TAG = ZipRunApp.class.getCanonicalName();
    public static ZipRunApp APPLICATION;
    private static Context context;
    protected ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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

    public String getAppURL() {
        return BuildConfig.APP_URL;
    }


    public final static class Constants {

        public static final LatLng DEFAULT_CAMERA_POSITION = new LatLng(28.586086, 77.171541);
        public static final String CONTACT_NO = "+918882779999";

        public static final String[] REPORT_ISSUE_ADDRESS = {"dev@ziprun.in",
                "madhu@ziprun.in"};

        public static final String  REPORT_ISSUE_SUBJECT = "Issue With ZipRun";

        public static String API_KEY = "AIzaSyBc58zTmjGgsLR2N4RDkjiTN5HgBlwHUJo";

        public static String[] CITIES_SERVED = {
            "delhi", "gurgaon", "noida", "faridabad"
        };
    }
}

