package com.ziprun.consumer.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.ui.activity.ForActivity;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.consumer.utils.AndroidBus;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

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
    }

    @Override
    public void pause(){
    }

    @Override
    public void stop(){
        bus.unregister(this);
    }

    @Override
    public void destroy(){
    }




    public void setBooking(@Nullable String bookingJson){
        booking = Booking.fromJson(bookingJson);
    }


}
