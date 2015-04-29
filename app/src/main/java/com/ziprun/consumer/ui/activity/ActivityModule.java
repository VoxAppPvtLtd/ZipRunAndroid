package com.ziprun.consumer.ui.activity;

import android.content.Context;

import com.ziprun.consumer.ui.custom.AddressAutocompleteView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;


@Module(injects = {
        SplashActivity.class,
        IntroActivity.class,
        IntroPagerAdapter.class,
        LoginActivity.class,
        DeliveryActivity.class,
        AddressAutocompleteView.class,
    }, complete = false,
       library = true)
public class ActivityModule {
    private static final String TAG = ActivityModule.class.getCanonicalName();
    private final ZipBaseActivity activity;

    public ActivityModule(ZipBaseActivity activity) {
        this.activity = activity;
    }

    @Provides @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    ReactiveLocationProvider provideReactiveLocationProvider(){
        return new ReactiveLocationProvider(activity);
    }


}
