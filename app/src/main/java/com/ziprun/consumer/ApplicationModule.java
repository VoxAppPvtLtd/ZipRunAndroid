package com.ziprun.consumer;

import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ziprun.consumer.utils.GoogleMapService;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(library = true, injects={ZipRunApp.class, Utils.class})
public class ApplicationModule {
    private static final String TAG = ApplicationModule.class.getCanonicalName();

    private final Application application;

    public ApplicationModule(ZipRunApp zipRunApp) {
        application = zipRunApp;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    GoogleMapService providesGoogleMapService(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api")
                .build();

        return restAdapter.create(GoogleMapService.class);
    }

    @Provides
    InputMethodManager provideInputMethodManager(){
        return (InputMethodManager)
                application.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    Display providesDisplay(){
        return ((WindowManager) application.getSystemService(Context
                .WINDOW_SERVICE)).getDefaultDisplay();
    }


}
