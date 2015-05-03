package com.ziprun.consumer.presenter;

import com.ziprun.consumer.data.model.AddressLocationPair;
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;

public class SourceLocationPickerPresenter extends LocationPickerPresenter {
    private static final String TAG = SourceLocationPickerPresenter.class.getCanonicalName();

    public SourceLocationPickerPresenter(LocationPickerFragment fragment) {
        super(fragment);
    }


    @Override
    public AddressLocationPair getSelectedLocaion() {
        return bookingLeg.getSource();
    }

    @Override
    public void moveForward() {
        bus.post(new OnSourceLocationSet()) ;
    }

}
