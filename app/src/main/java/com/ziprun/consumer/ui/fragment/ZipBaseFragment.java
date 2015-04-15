package com.ziprun.consumer.ui.fragment;

import android.os.Bundle;

import com.ziprun.consumer.ui.activity.ZipBaseActivity;

import dagger.ObjectGraph;

public abstract class ZipBaseFragment extends BackHandlerFragment {

    private ObjectGraph fragmentGraph;

    protected ZipBaseActivity activity;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ZipBaseActivity) getActivity();
        fragmentGraph = activity.getActivityGraph().plus(getModules());
        fragmentGraph.inject(this);
    }

    protected Object[] getModules() {
        return new Object[] {new FragmentModule(this)};
    }

    public void inject(Object object) {
        fragmentGraph.inject(object);
    }

}
