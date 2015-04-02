package com.ziprun.consumer.activity;

import dagger.Module;


@Module(injects = {LoginActivity.class, MainActivity.class}, complete = false,
        library = true)
public class ActivityModule {
    private static final String TAG = ActivityModule.class.getCanonicalName();
    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
