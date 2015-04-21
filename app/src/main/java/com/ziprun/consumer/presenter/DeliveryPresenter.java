package com.ziprun.consumer.presenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.data.model.BookingLeg;
import com.ziprun.consumer.ui.activity.ForActivity;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.consumer.utils.AndroidBus;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public abstract class  DeliveryPresenter implements PresenterInterface {
    private static final String TAG = DeliveryPresenter.class.getCanonicalName();

    @Inject
    Utils utils;

    @ForActivity
    @Inject
    Context context;

    @Inject
    AndroidBus bus;

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
        Log.i(TAG, "Composite Subscription Created ");
    }

    @Override
    public void pause(){
    }

    @Override
    public void stop(){
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
        booking = Booking.fromJson(bookingJson);
        this.currentLeg = currentLeg;
        bookingLeg = booking.getBookingLeg(this.currentLeg);
        Log.i(TAG, "Inside: " + this.getClass().getSimpleName() + " " +
                bookingLeg.toJson());
    }

    public abstract void moveForward();
}
