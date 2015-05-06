package com.ziprun.consumer.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ziprun.consumer.ZipEventTracker;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.data.model.BookingLeg;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.activity.DeliveryActivity;
import com.ziprun.consumer.ui.activity.ForActivity;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.consumer.utils.AndroidBus;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class DeliveryPresenter implements PresenterInterface {
    private static final String TAG = DeliveryPresenter.class.getCanonicalName();

    @Inject
    Utils utils;

    @ForActivity
    @Inject
    Context context;

    @Inject
    AndroidBus bus;

    @Inject
    ZipEventTracker eventTracker;

    protected DeliveryFragment view;
    protected Booking booking;
    protected int currentLeg;
    protected BookingLeg bookingLeg;

    protected CompositeSubscription compositeSubscription;


    public DeliveryPresenter(DeliveryFragment view){
        this.view = view;
    }

    @Override
    public void initialize() {
        view.inject(this);
    }

    @Override
    public void start(){
        bus.register(this);
        compositeSubscription = new CompositeSubscription();
        eventTracker.trackAppFlow(getClass().getSimpleName());
    }

    @Override
    public void pause(){
    }

    @Override
    public void stop(){
        updateBooking();
        bus.unregister(this);
        if(compositeSubscription != null){
            compositeSubscription.clear();
            compositeSubscription = null;
        }
    }

    @Override
    public void destroy(){
    }

    public void setBooking(@Nullable String bookingJson, int currentLeg){
        booking = Booking.fromJson(bookingJson, Booking.class);
        this.currentLeg = currentLeg;
        bookingLeg = booking.getBookingLeg(this.currentLeg);
        Timber.d("Inside: " + this.getClass().getSimpleName() + " " +
                bookingLeg.toJson());
    }

    public abstract void moveForward();

    public BookingLeg getBookingLeg() {
        return bookingLeg;
    }

    public  void saveInstanceState(Bundle outState){
        outState.putString(DeliveryActivity.KEY_BOOKING, booking.toJson());
        outState.putInt(DeliveryActivity.KEY_CURRENT_LEG, currentLeg);
    }

    public void updateBooking(){
        bus.post(new UpdateBookingEvent(booking));
    }

}
