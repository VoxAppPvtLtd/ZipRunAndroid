package com.ziprun.consumer.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.data.model.RideType;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.consumer.ui.fragment.SummaryFragment;

public class SummaryPresenter extends DeliveryPresenter {
    private static final String TAG = SummaryPresenter.class.getCanonicalName();

    private SummaryFragment summaryView;

    public SummaryPresenter(DeliveryFragment view) {
        super(view);
        summaryView = (SummaryFragment) view;
    }

    @Override
    public void moveForward() {

    }

    public String getInstruction() {
        return bookingLeg.getUserInstructions();
    }

    public RideType getBookingType(){
        return bookingLeg.getRideType();
    }

    public LatLng getSourceLatLng(){
        return bookingLeg.getSourceLatLng();
    }

    public LatLng getDestinationLatLng(){
        return bookingLeg.getDestinationLatLng();
    }

    public String getSourceAddress(){
        return bookingLeg.getSourceAddress();
    }

    public String getDestinationAddress(){
        return bookingLeg.getDestinationAddress();
    }

    public double getEstimateDistance() {
        return bookingLeg.getEstimatedDistance();
    }

    public double getEstimatedCost() {
        return bookingLeg.getEstimatedCost();
    }

    public int getTransactionCost(){
        return booking.getRateCard().getTransactionCost();
    }


}
