package com.ziprun.consumer.ui.fragment;

import dagger.Module;

@Module(injects = {

    }, complete = false,
    library = true)
public class FragmentModule {
    private static final String TAG = FragmentModule.class.getCanonicalName();
    private ZipBaseFragment zipFragment;

    public FragmentModule(ZipBaseFragment zipFragment) {
        this.zipFragment = zipFragment;
    }
}
