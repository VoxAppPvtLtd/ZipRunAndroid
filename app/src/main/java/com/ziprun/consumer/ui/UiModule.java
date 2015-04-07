package com.ziprun.consumer.ui;

import com.ziprun.consumer.ui.activity.MainActivity;

import dagger.Module;

@Module(
    injects = {
        MainActivity.class,
    },
    complete = false,
    library = true
)
public final class UiModule {
}
