package com.ziprun.consumer.network;

import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.data.model.DeliveryRateCard;
import com.ziprun.consumer.data.model.ZipConsumer;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

public interface ZipRestApi {
    @POST("/consumer/login")
    Observable<ZipConsumer>
        login(@Header("X-Auth-Service-Provider") String serviceProvider,
              @Header("X-Verify-Credentials-Authorization") String authoriztion,
              @Field("userID") String userID,
              @Field("mobileNumber") String mobileNumber);

    @GET("/consumer/ratecard")
    Observable<DeliveryRateCard> getRateCard(Booking booking);

    @POST("/boooking")
    Observable<Booking> createBooking(Booking booking);

}
