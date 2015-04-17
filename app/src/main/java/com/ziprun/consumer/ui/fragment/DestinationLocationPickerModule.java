package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.presenter.DestinationLocationPickerPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        DestinationLocationPickerFragment.class,
        DestinationLocationPickerPresenter.class
    }, complete = false)

public class DestinationLocationPickerModule {
    private static final String TAG = DestinationLocationPickerModule.class.getCanonicalName();

    protected LocationPickerFragment fragment;

    public DestinationLocationPickerModule(LocationPickerFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideDestinationLocationPresenter(){
        return new DestinationLocationPickerPresenter(fragment);
    }

}
