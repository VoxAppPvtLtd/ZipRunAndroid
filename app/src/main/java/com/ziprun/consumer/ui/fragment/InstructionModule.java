package com.ziprun.consumer.ui.fragment;


import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.presenter.InstructionPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects= {
        InstructionFragment.class,
        InstructionPresenter.class,
    }, complete = false)
public class InstructionModule {
    private static final String TAG = InstructionModule.class.getCanonicalName();

    protected InstructionFragment fragment;

    public InstructionModule(InstructionFragment zipFragment) {
        fragment = zipFragment;
    }

    @Singleton
    @Provides
    public DeliveryPresenter provideInstructionPresenter(){
        return new InstructionPresenter(fragment);
    }

}
