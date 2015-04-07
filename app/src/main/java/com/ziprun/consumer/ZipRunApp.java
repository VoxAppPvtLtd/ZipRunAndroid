package com.ziprun.consumer;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class ZipRunApp extends Application {
    private static final String TAG = ZipRunApp.class.getCanonicalName();
    public static ZipRunApp APPLICATION;
    private static Context context;
    protected ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
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
}

