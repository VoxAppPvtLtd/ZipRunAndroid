package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.presenter.SourceLocationPickerPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        SourceLocationPickerFragment.class,
        SourceLocationPickerPresenter.class
    }, complete = false)

public class SourceLocationPickerModule {
    private static final String TAG = SourceLocationPickerModule.class.getCanonicalName();

    protected LocationPickerFragment fragment;

    public SourceLocationPickerModule(LocationPickerFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideSourceLocationPresenter(){
        return new SourceLocationPickerPresenter(fragment);
    }



}
