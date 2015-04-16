package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.presenter.SourceLocationPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        LocationPickerFragment.class
    }, complete = false)
public class LocationPickerModule {
    private static final String TAG = LocationPickerModule.class.getCanonicalName();

    protected LocationPickerFragment fragment;

    public LocationPickerModule(LocationPickerFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideSourceLocationPresenter(){
        return new SourceLocationPresenter(fragment);
    }

}
