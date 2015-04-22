package com.ziprun.consumer.ui.fragment;

import android.os.Bundle;

import com.ziprun.consumer.ui.activity.ZipBaseActivity;
import com.ziprun.consumer.utils.AndroidBus;

import javax.inject.Inject;

import dagger.ObjectGraph;

public abstract class ZipBaseFragment extends BackHandlerFragment {

    private ObjectGraph fragmentGraph;

    protected ZipBaseActivity activity;

    @Inject
    AndroidBus bus;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ZipBaseActivity) getActivity();
        fragmentGraph = activity.getActivityGraph().plus(getModules());
        fragmentGraph.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    protected Object[] getModules() {
        return new Object[] {new FragmentModule(this)};
    }

    public void inject(Object object) {
        fragmentGraph.inject(object);
    }

}
