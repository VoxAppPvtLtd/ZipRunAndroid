package com.ziprun.consumer.presenter;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.data.ZipRunSession;
import com.ziprun.consumer.data.model.DeliveryRateCard;
import com.ziprun.consumer.data.model.RideType;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.ConfirmationFragment;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.maputils.GoogleDirectionAPI;
import com.ziprun.maputils.models.Directions;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ConfirmationPresenter extends DeliveryPresenter {
    private static final String TAG = ConfirmationPresenter.class.getCanonicalName();

    private ConfirmationFragment confirmationView;

    private Observable<Directions> directionsObservable;

    private boolean isMapReady;

    private boolean directionFetched;

    private Directions deliveryDirection;

    @Inject
    ZipRunSession zipSession;

    public ConfirmationPresenter(DeliveryFragment view) {
        super(view);
        confirmationView = (ConfirmationFragment) view;

    }

    @Override
    public void start() {
        super.start();
        fetchDirections();

    }

    @Override
    public void stop() {
        super.stop();
        updateBooking();
        isMapReady = false;
        directionFetched = false;
    }

    @Override
    public void moveForward() {

    }

    private void updateBooking() {
        String instruction = confirmationView.getInstruction();
        bookingLeg.setUserInstructions(instruction);
        bus.post(new UpdateBookingEvent(booking));
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
        return bookingLeg.getDesinationAddress();
    }


    private void fetchDirections() {
        if(directionsObservable == null){
            directionsObservable = new GoogleDirectionAPI(ZipRunApp.Constants
                    .API_KEY, bookingLeg.getSource().latLng,
                    bookingLeg.getDestination().latLng).getDirections();

        }

        compositeSubscription.add(directionsObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(directionGetter));
    }

    public void onMapReady(){
        isMapReady = true;
    }

    private Action1<Directions> directionGetter  = new Action1<Directions>() {
        @Override
        public void call(Directions directions) {

            Log.i(TAG, "Directions Fetched ");
            deliveryDirection = directions;
            directionFetched = true;
            showDirection();
        }
    };


    public void showDirection(){
        if(!isMapReady || !directionFetched)
            return;

        DeliveryRateCard rateCard = zipSession.getRateCard();

        int distance = (int) Math.ceil(deliveryDirection.getTotalDistance(0) / 1000);

        int cost = distance  * rateCard.getRatePerKm();

        if(cost < rateCard.getMinPrice()){
            cost = rateCard.getMinPrice();
        }

        Log.i(TAG, "Distance " +  distance + " Cost " + cost);

        bookingLeg.setEstimatedDistance(distance);
        bookingLeg.setEstimatedCost(cost);

        List <LatLng> points = deliveryDirection.getPoints(0);

        confirmationView.drawRoute(points);

        confirmationView.showEstimates(distance, rateCard.getRatePerKm(),  cost,
                rateCard.getTransactionCost());

    }

    public boolean hasDirections(){
        return directionFetched;
    }

    public String getInstruction() {
        return bookingLeg.getUserInstructions();
    }

    public RideType getBookingType(){
        return bookingLeg.getRideType();
    }
}
