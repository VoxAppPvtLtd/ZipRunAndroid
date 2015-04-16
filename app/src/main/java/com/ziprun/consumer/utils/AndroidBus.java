package com.ziprun.consumer.utils;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class AndroidBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public AndroidBus(ThreadEnforcer threadEnforcer) {
        super(threadEnforcer);
    }

    public AndroidBus() {
        super(ThreadEnforcer.ANY);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    AndroidBus.super.post(event);
                }
            });
        }
    }
}
