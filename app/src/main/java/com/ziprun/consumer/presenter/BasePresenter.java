package com.ziprun.consumer.presenter;


import android.content.Context;

import com.ziprun.consumer.ui.activity.ForActivity;
import com.ziprun.consumer.utils.AndroidBus;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

public abstract class BasePresenter {
    private static final String TAG = BasePresenter.class.getCanonicalName();

    @Inject
    Utils utils;

    @ForActivity
    @Inject
    Context context;

    @Inject
    AndroidBus bus;


    public BasePresenter(){
    }

    public void initialize(){
    }

    public void start(){
        bus.register(this);
    }

    public void pause(){
    }

    public void stop(){
        bus.unregister(this);
    }

    public void destroy(){

    }
}
