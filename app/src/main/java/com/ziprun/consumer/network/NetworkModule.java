package com.ziprun.consumer.network;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class NetworkModule {
    private static final String TAG = NetworkModule.class.getCanonicalName();


    @Singleton
    @Provides
    ZipRestApi providesZipRestApiClient(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        return restAdapter.create(ZipRestApi.class);
    }
}
