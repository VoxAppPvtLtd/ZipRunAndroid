package com.ziprun.consumer.presenter;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.data.ZipRunSession;
import com.ziprun.consumer.data.model.DeliveryRateCard;
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
        isMapReady = false;
        directionFetched = false;
    }
    
    public LatLng getSourceLatLng(){
        return booking.getSourceLatLng();
    }

    public LatLng getDestinationLatLng(){
        return booking.getDestinationLatLng();
    }

    public String getSourceAddress(){
        return booking.getSourceAddress();
    }

    public String getDestinationAddress(){
        return booking.getDesinationAddress();
    }

    private void fetchDirections() {
        if(directionsObservable == null){
            directionsObservable = new GoogleDirectionAPI(ZipRunApp.Constants
                    .API_KEY, booking.getSourceLocation().latLng,
                    booking.getDestLocation().latLng).getDirections();

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

        int distance = deliveryDirection.getTotalDistance(0);

        int cost = distance / 1000 * rateCard.getRatePerKm();

        Log.i(TAG, "Distance " +  distance + " Cost " + cost);

        booking.setEstimateDistance(distance);
        booking.setEstimateCost(cost);

        List <LatLng> points = deliveryDirection.getPoints(0);

        confirmationView.drawRoute(points);

        confirmationView.showEstimates(distance, cost,
                rateCard.getTransactionCost());

    }
}
