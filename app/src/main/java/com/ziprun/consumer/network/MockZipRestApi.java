    //package com.ziprun.consumer.network;
    //
    //import com.ziprun.consumer.data.ZipRunSession;
    //import com.ziprun.consumer.data.model.Booking;
    //import com.ziprun.consumer.data.model.DeliveryRateCard;
    //import com.ziprun.consumer.data.model.ZipConsumer;
    //
    //import java.util.concurrent.TimeUnit;
    //
    //import javax.inject.Inject;
    //
    //import retrofit.client.Response;
    //import retrofit.http.Body;
    //import rx.Observable;
    //
    //public class MockZipRestApi implements ZipRestApi {
    //
    //    @Inject
    //    ZipRunSession zipRunSession;
    //
    //    @Inject
    //    public MockZipRestApi(){
    //    }
    //
    //
    //    @Override
    //    public Observable<Response> verifyMobileNumber(@Body String mobileNumber) {
    //        return null;
    //    }
    //
    //    @Override
    //    public Observable<ZipConsumer> verifyOTP(@Body String otp) {
    //        return null;
    //    }
    //
    //    @Override
    //    public Observable<DeliveryRateCard> getRateCard(Booking booking) {
    //        return Observable.just(zipRunSession.getRateCard()).delay(5,
    //                TimeUnit.SECONDS);
    //    }
    //
    //    @Override
    //    public Observable<Booking> createBooking(Booking booking) {
    //        return null;
    //    }
    //}
