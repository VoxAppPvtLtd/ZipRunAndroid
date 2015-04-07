package com.ziprun.consumer.ui.activity;

import dagger.Module;


@Module(injects = {LoginActivity.class, MainActivity.class}, complete = false,
        library = true)
public class ActivityModule {
    private static final String TAG = ActivityModule.class.getCanonicalName();
    private final ZipBaseActivity activity;

    public ActivityModule(ZipBaseActivity activity) {
        this.activity = activity;
    }
}
