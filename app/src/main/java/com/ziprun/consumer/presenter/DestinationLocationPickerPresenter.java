package com.ziprun.consumer.presenter;

import com.ziprun.consumer.data.model.AddressLocationPair;
import com.ziprun.consumer.event.OnDestinationSet;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;

public class DestinationLocationPickerPresenter extends LocationPickerPresenter {
    private static final String TAG = DestinationLocationPickerPresenter.class.getCanonicalName();

    public DestinationLocationPickerPresenter(LocationPickerFragment fragment) {
        super(fragment);
    }

    @Override
    public AddressLocationPair getSelectedLocaion() {
        return booking.getDestLocation();
    }

    @Override
    public void moveForward() {
        bus.post(new OnDestinationSet());
    }
}
