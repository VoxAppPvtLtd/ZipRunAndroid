package com.ziprun.consumer.network;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziprun.consumer.data.model.BookingLeg;
import com.ziprun.consumer.utils.AnnotationExclusionStrategy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

@Module(
        complete = false,
        library = true
)
public class NetworkModule {
    private static final String TAG = NetworkModule.class.getCanonicalName();

    @Singleton
    @Provides
    RequestInterceptor providesRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("WWW-Authenticate", "Token");
                request.addHeader("Authorization", "Token 2e97ecb2f1105bfadfabb6b854daccc144022ca2");
            }
        };

    }

    @Singleton
    @Provides
    RestAdapter providesRestAdapter(RequestInterceptor interceptor){

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegSerializer())
                .registerTypeAdapter(BookingLeg.class,
                        new BookingLeg.BookingLegDeserializer())
                .create();

        return new RestAdapter.Builder()
                .setEndpoint("http://192.168.43.56:8000/api")
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(interceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog(TAG))
                .build();
    }

    @Singleton
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
