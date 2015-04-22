package com.ziprun.consumer.network;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziprun.consumer.data.model.BookingLeg;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(
        complete = false,
        library = true
)
public class NetworkModule {
    private static final String TAG = NetworkModule.class.getCanonicalName();

    @Singleton
    @Provides
    RestAdapter providesRestAdapter(){

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegSerializer())
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegDeserializer())
                .create();

        return new RestAdapter.Builder()
                .setEndpoint("https://app.ziprun.in")
                .setConverter(new GsonConverter(gson))
                .build();
    }

//    @Singleton
//    @Provides
//    ZipRestApi providesZipRestApiClient(RestAdapter restAdapter){
//        return restAdapter.create(ZipRestApi.class);
//    }

    @Singleton
    @Provides
    ZipRestApi providesZipRestApiClient(MockZipRestApi restApi){
        return restApi;
    }

}
