package com.ziprun.consumer.network;

import com.ziprun.consumer.data.model.AuthOTP;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.data.model.DeliveryRateCard;
import com.ziprun.consumer.data.model.ZipConsumer;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface ZipRestApi {
//    @POST("/consumer/login")
//    Observable<ZipConsumer>
//        login(@Header("X-Auth-Service-Provider") String serviceProvider,
//              @Header("X-Verify-Credentials-Authorization") String authoriztion,
//              @Field("userID") String userID,
//              @Field("mobileNumber") String mobileNumber);

    @POST("/verify_number/")
    Observable<Response> verifyMobileNumber(@Body AuthOTP authOTP);

    @POST("/verify_otp/")
    Observable<ZipConsumer> verifyOTP(@Body AuthOTP authOTP);


    @POST("/booking_rate/")
    Observable<DeliveryRateCard> getRateCard(@Body Booking booking);

    @POST("/booking/")
    Observable<Booking> createBooking(@Body Booking booking);

}



