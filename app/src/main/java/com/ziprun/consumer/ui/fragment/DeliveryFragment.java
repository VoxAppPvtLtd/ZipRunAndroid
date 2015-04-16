package com.ziprun.consumer.ui.fragment;

import android.os.Bundle;

import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.ui.activity.DeliveryActivity;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

public abstract  class DeliveryFragment extends ZipBaseFragment {
    private static final String TAG = DeliveryFragment.class.getCanonicalName();

    @Inject
    DeliveryPresenter presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        presenter.initialize();
        presenter.setBooking(args.getString(DeliveryActivity.KEY_BOOKING));

    }

    @Override
    protected Object[] getModules() {
        ArrayList<Object> modules = new ArrayList<>(
                Arrays.asList(super.getModules()));

        modules.add(getCurrentModule());

        return modules.toArray();
    }

    protected abstract Object getCurrentModule();
}
