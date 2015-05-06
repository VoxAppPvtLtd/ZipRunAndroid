package com.ziprun.consumer;

import android.content.Context;

import com.appsflyer.AppsFlyerLib;
import com.ziprun.consumer.data.ZipRunSession;
import com.ziprun.consumer.data.model.ZipConsumer;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ZipEventTracker {
    private static final String TAG = ZipEventTracker.class.getCanonicalName();

    @Inject
    @ForApplication
    Context appContext;

    @Inject
    Utils utils;

    @Inject
    ZipRunSession zipRunSession;



    public ZipEventTracker(){
    }

    public void authenticateUser(){
        ZipConsumer consumer = zipRunSession.getConsumer();
        AppsFlyerLib.sendTrackingWithEvent(appContext, "registration", "");
        AppsFlyerLib.setAppUserId(consumer.getMobileNumber());
    }


    public void trackAppFlow(String flowName){
        AppsFlyerLib.sendTrackingWithEvent(appContext, flowName, "");
    }

    public void newBooking() {
        AppsFlyerLib.sendTrackingWithEvent(appContext, "Booking", "");
    }
}
