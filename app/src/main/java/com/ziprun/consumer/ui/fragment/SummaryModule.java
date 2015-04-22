package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.SummaryPresenter;
import com.ziprun.consumer.presenter.DeliveryPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        SummaryFragment.class,
        SummaryPresenter.class,
    },
    complete = false)


public class SummaryModule {
    private static final String TAG = SummaryModule.class.getCanonicalName();

    protected SummaryFragment fragment;

    public SummaryModule(SummaryFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideSummaryPresenter(){
        return new SummaryPresenter(fragment);
    }

}
