package com.ziprun.consumer.network;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.data.model.BookingLeg;
import com.ziprun.consumer.managers.TokenRequestInterceptor;
import com.ziprun.consumer.utils.AnnotationExclusionStrategy;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

@Module(
        complete = false,
        library = true
)
public class NetworkModule {
    private static final String TAG = NetworkModule.class.getCanonicalName();

    @Provides
    RestAdapter providesRestAdapter(TokenRequestInterceptor interceptor){

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegSerializer())
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegDeserializer())
                .create();

        return new RestAdapter.Builder()
                .setEndpoint(ZipRunApp.APPLICATION.getAppURL() + "api/")
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(interceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog(TAG))
                .build();
    }

    @Provides
    ZipRestApi providesZipRestApiClient(RestAdapter restAdapter){
        return restAdapter.create(ZipRestApi.class);
    }

//    @Singleton
//    @Provides
//    ZipRestApi providesZipRestApiClient(MockZipRestApi restApi){
//        return restApi;
//    }

}
