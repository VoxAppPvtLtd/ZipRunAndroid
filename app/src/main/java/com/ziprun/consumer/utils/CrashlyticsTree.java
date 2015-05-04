package com.ziprun.consumer.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsTree extends Timber.Tree {
    private static final String TAG = CrashlyticsTree.class.getCanonicalName();

    @Override
    public void v(String message, Object... args) {
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
    }

    @Override
    public void d(String message, Object... args) {
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
    }

    @Override
    public void wtf(String message, Object... args) {
        logMessage("WTF:" + message, args);
    }

    @Override
    public void wtf(Throwable t, String message, Object... args) {
        logMessage("WTF:" + message, args);
    }

    @Override
    public void i(String message, Object... args) {
        logMessage(message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        logMessage(message, args);
        // NOTE: We are explicitly not sending the exception to Crashlytics here.
    }

    @Override
    public void w(String message, Object... args) {
        logMessage("WARN: " + message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        logMessage("WARN: " + message, args);
        // NOTE: We are explicitly not sending the exception to Crashlytics here.
    }

    @Override
    public void e(String message, Object... args) {
        logMessage("ERROR: " + message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        logMessage("ERROR: " + message, args);
        Crashlytics.logException(t);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(priority < Log.INFO)
            return;

        if(priority == Log.INFO){
            i(tag, message);
        }else if(priority == Log.WARN){
            w(tag, message);
        }else if(priority == Log.ERROR){
            e(t, tag, message);
        }
    }

    private void logMessage(String message, Object... args) {
        Crashlytics.log(String.format(message, args));
    }
}
