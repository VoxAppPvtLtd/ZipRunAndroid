package com.ziprun.consumer.presenter;


import android.content.Context;

import com.ziprun.consumer.ui.activity.ForActivity;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

public abstract class BasePresenter {
    private static final String TAG = BasePresenter.class.getCanonicalName();

    @Inject
    Utils utils;

    @ForActivity
    @Inject
    Context context;


    public BasePresenter(){
    }

    public abstract void initialize();

    public abstract void start();

    public abstract void pause();

    public abstract void stop();

    public abstract void destroy();
}
