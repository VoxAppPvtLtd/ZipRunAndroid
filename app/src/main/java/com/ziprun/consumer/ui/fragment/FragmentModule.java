package com.ziprun.consumer.ui.fragment;

import com.ziprun.consumer.presenter.SourceLocationPresenter;

import dagger.Module;

@Module(injects = {
        LocationPickerFragment.class,
        InstructionFragment.class,
        SummaryFragment.class,
        SourceLocationPresenter.class,
    }, complete = false,
    library = true)
public class FragmentModule {
    private static final String TAG = FragmentModule.class.getCanonicalName();
    private ZipBaseFragment zipFragment;

    public FragmentModule(ZipBaseFragment zipFragment) {
        this.zipFragment = zipFragment;
    }

}
