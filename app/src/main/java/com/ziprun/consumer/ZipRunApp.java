    package com.ziprun.consumer;

    import android.app.Application;
import android.content.Context;

import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.data.model.ZipConsumer;
import com.ziprun.consumer.utils.CrashlyticsTree;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

    public class ZipRunApp extends Application {

    private static final String TAG = ZipRunApp.class.getCanonicalName();
    public static ZipRunApp APPLICATION;
    private static Context context;
    protected ObjectGraph applicationGraph;

    @Inject
    ZipConsumer zipConsumer;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        setupLogger();
        APPLICATION = this;
        ZipRunApp.context = getApplicationContext();
        applicationGraph = ObjectGraph.create(getModules());
        applicationGraph.inject(this);
        setupAppsFlyerSDK();
    }

    private void setupLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree());
        }
    }

    private void setupAppsFlyerSDK(){
        AppsFlyerLib.setAppsFlyerKey(Constants.APPSFLYER_DEV_KEY);
        AppsFlyerLib.setCurrencyCode(Constants.ISO_CURRENCY_CODE);
        if(zipConsumer != null){
            AppsFlyerLib.setAppUserId(zipConsumer.getMobileNumber());
        }
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
        public static final String APPSFLYER_DEV_KEY = "G2tjmajjYFqKPNTjgLSnW";
        public static final String ISO_CURRENCY_CODE = "INR";

        public static final String[] REPORT_ISSUE_ADDRESS = {"hello@ziprun.in"};

        public static final String  REPORT_ISSUE_SUBJECT = "Issue With ZipRun";
        public static final LatLng DELHI_LATLNG = new LatLng(28.586086, 77.171541);

        public static String API_KEY = "AIzaSyBc58zTmjGgsLR2N4RDkjiTN5HgBlwHUJo";

        public static String[] CITIES_SERVED = {
            "delhi", "gurgaon", "noida", "faridabad"
        };
    }
}

