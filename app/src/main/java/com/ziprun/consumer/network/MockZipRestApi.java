package com.ziprun.consumer.network;

import com.ziprun.consumer.data.ZipRunSession;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.data.model.DeliveryRateCard;
import com.ziprun.consumer.data.model.ZipConsumer;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.http.Field;
import retrofit.http.Header;
import rx.Observable;

public class MockZipRestApi implements ZipRestApi {

    @Inject
    ZipRunSession zipRunSession;

    @Inject
    public MockZipRestApi(){
    }

    @Override
    public Observable<ZipConsumer> login(
        @Header("X-Auth-Service-Provider")  String serviceProvider,
        @Header("X-Verify-Credentials-Authorization") String authoriztion,
        @Field("userID") String userID, @Field("mobileNumber") String mobileNumber) {

        return null;
    }

    @Override
    public Observable<DeliveryRateCard> getRateCard(Booking booking) {
        return Observable.just(zipRunSession.getRateCard()).delay(5,
                TimeUnit.SECONDS);
    }

    @Override
    public Observable<Booking> createBooking(Booking booking) {
        return null;
    }
}
