package com.ziprun.consumer.presenter;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.data.ZipRunSession;
import com.ziprun.consumer.data.model.DeliveryRateCard;
import com.ziprun.consumer.data.model.RideType;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.network.ZipRestApi;
import com.ziprun.consumer.ui.fragment.ConfirmationFragment;
import com.ziprun.consumer.ui.fragment.DeliveryFragment;
import com.ziprun.maputils.GoogleDirectionAPI;
import com.ziprun.maputils.models.Directions;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ConfirmationPresenter extends DeliveryPresenter {
    private static final String TAG = ConfirmationPresenter.class.getCanonicalName();

    private ConfirmationFragment confirmationView;

    private Observable<Directions> directionsObservable;

    private Observable<DeliveryRateCard> rateCardObservable;

    private Observable<Boolean> zipObservable;

    private boolean isMapReady;

    private boolean directionFetched;

    private Directions deliveryDirection;

    private DeliveryRateCard rateCard;

    @Inject
    ZipRunSession zipSession;

    @Inject
    ZipRestApi zipRestApi;

    public ConfirmationPresenter(DeliveryFragment view) {
        super(view);
        confirmationView = (ConfirmationFragment) view;
    }

    @Override
    public void initialize() {
        super.initialize();
        directionFetched = false;
        isMapReady = false;
    }

    @Override
    public void start() {
        super.start();
        fetchDirectionsAndRateCard();

    }

    @Override
    public void stop() {
        super.stop();
        updateBooking();
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
        return bookingLeg.getDestinationAddress();
    }

    private void fetchDirectionsAndRateCard(){
        if(!directionFetched) {
            fetchDirections();
            fetchRateCard();

            if(zipObservable == null){
                zipObservable =  Observable.zip(directionsObservable, rateCardObservable,
                    new Func2<Directions, DeliveryRateCard, Boolean>() {
                        @Override
                        public Boolean call(Directions directions,
                                            DeliveryRateCard deliveryRateCard) {

                            Log.i(TAG, "Directions and Delivery Rate Card Fetched");
                            rateCard = deliveryRateCard;
                            deliveryDirection = directions;
                            directionFetched = true;
                            return true;
                        }
                    }).cache()
                    .observeOn(AndroidSchedulers.mainThread());
            }
        }

        Log.i(TAG, "Subscribing to zipObservable");
        compositeSubscription.add(zipObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean bool) {
                Log.i(TAG, "Show Directions called");
                showDirection();
            }
        }));
    }


    private void fetchDirections() {
        if(directionsObservable == null){
            directionsObservable = new GoogleDirectionAPI(ZipRunApp.Constants
                    .API_KEY, bookingLeg.getSource().latLng,
                    bookingLeg.getDestination().latLng).getDirections();
        }

        directionsObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
    }

    private void fetchRateCard(){
        if(rateCardObservable == null){
            rateCardObservable = zipRestApi.getRateCard(booking);
        }

        rateCardObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();

    }

    public void onMapReady(){
        isMapReady = true;
    }

    public void showDirection(){
        if(!isMapReady || !directionFetched)
            return;


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

        confirmationView.showEstimates(distance, rateCard.getRatePerKm(), cost,
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
