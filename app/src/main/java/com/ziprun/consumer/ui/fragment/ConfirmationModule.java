package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.presenter.ConfirmationPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        ConfirmationFragment.class,
        ConfirmationPresenter.class,
    }, complete = false)


public class ConfirmationModule {
    private static final String TAG = ConfirmationModule.class.getCanonicalName();

    protected ConfirmationFragment fragment;

    public ConfirmationModule(ConfirmationFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideConfirmationPresenter(){
        return new ConfirmationPresenter(fragment);
    }

}
