package com.ziprun.consumer;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class ZipRunApplication extends Application {
    private static final String TAG = ZipRunApplication.class.getCanonicalName();
    public static ZipRunApplication APPLICATION;
    private static Context context;
    protected ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        APPLICATION = this;
        ZipRunApplication.context = getApplicationContext();
        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);
    }

    public static Context getAppContext() {
        return ZipRunApplication.context;
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

    protected List<Object> getModules() {

        return new ArrayList<Object>(Arrays.asList(new Object[]{new
                ApplicationModule(this)}));
    }    
}

