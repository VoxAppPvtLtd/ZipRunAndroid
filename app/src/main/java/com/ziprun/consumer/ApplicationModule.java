package com.ziprun.consumer;

import android.app.Application;
import android.content.Context;

import com.ziprun.consumer.utils.GoogleMapService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(library = true, injects={ZipRunApp.class})
public class ApplicationModule {
    private static final String TAG = ApplicationModule.class.getCanonicalName();

    private final Application application;

    public ApplicationModule(ZipRunApp zipRunApp) {
        application = zipRunApp;
    }

    @Provides
    @Singleton
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


}
