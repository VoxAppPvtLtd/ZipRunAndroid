package com.ziprun.consumer.presenter;

import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.data.model.AddressLocationPair;
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;
import com.ziprun.consumer.utils.RetryWithDelay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SourceLocationPresenter extends DeliveryPresenter {
    private static final String TAG = SourceLocationPresenter.class.getCanonicalName();

    private static final LocationRequest locationRequest =
            LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setSmallestDisplacement(10)
                    .setInterval(100);

    @Inject
    ReactiveLocationProvider locationProvider;

    private LocationPickerFragment locationPickerView;

    private Location currentLocation;

    private LatLng currentLatLng;

    private AddressLocationPair sourceLocation;

    Subscription geocodeSubscription;

    private CompositeSubscription compositeSubscription;

    private Boolean locationEnabledFlag = null;

    private boolean isMapReady =  false;

    private boolean performGeocode = false;

    private boolean firstLoad = true;

    public SourceLocationPresenter(LocationPickerFragment fragment){
        super(fragment);
        locationPickerView = fragment;
   }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void start() {
        super.start();
        Log.i(TAG, "Presenter Started");
        isMapReady = false;
        locationEnabledFlag = null;
        compositeSubscription = new CompositeSubscription();
        checkLocationSettings();
        setUpLocationUpdates();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void stop() {
        super.stop();
        compositeSubscription.clear();
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    @Override
    public void setBooking(@Nullable String bookingJson) {
        super.setBooking(bookingJson);
        sourceLocation = booking.getSourceLocation();
        if(sourceLocation == null){
            sourceLocation = new AddressLocationPair();
        }
    }

    private Action1<LocationSettingsResult> locationSettingsManager =
        new Action1<LocationSettingsResult>() {
            @Override
            public void call(LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                Log.i(TAG, "Location Settings Status " + status
                        .getStatusMessage());

                if (status.isSuccess()) {
                    enableLocationFlag(true);
                } else if (status.hasResolution()) {
                    locationPickerView.startResolutionActivity(status);
                } else {
                    enableLocationFlag(false);
                    currentLocation = null;
                }
            }
    };

    private Action1<Location> locationSetter = new Action1<Location>() {
        @Override
        public void call(Location location) {
            if(locationEnabledFlag != null && locationEnabledFlag == false)
                return;

            currentLocation = location;
            currentLatLng = utils.getLatLngFromLocation(currentLocation);
            Log.i(TAG, "New Location Updated " + location.toString());

            if(isMapReady){
               locationPickerView.setCurrentLocationMarker(currentLatLng);
            }

            if(sourceLocation.latLng == null)
                setSourceLocation();
        }
    };

    public void checkLocationSettings(){
        compositeSubscription.add(
                locationProvider.checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .build()
                ).subscribeOn(Schedulers.newThread())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(locationSettingsManager));

    }

    private void setUpLocationUpdates(){

        compositeSubscription.add(locationProvider.getLastKnownLocation()
                .concatWith(locationProvider.getUpdatedLocation
                        (locationRequest))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationSetter));
    }

    public void onMapReady(){
        Log.i(TAG, "Map Ready");
        isMapReady = true;
        setSourceLocation();
    }


    public void enableLocationFlag(boolean enabled){
        locationEnabledFlag = enabled;
        locationPickerView.showCurrentLocationBtn(enabled);
        setSourceLocation();
    }

    private void setSourceLocation(){
        if(!isMapReady || locationEnabledFlag == null)
            return;

        else if(sourceLocation.latLng != null && sourceLocation.address != null){
            // If Source Location is already set, we don't care about current
            // Location
            Log.i(TAG, "Move Camera to Existing Source Address");
            moveToSourceAddress();
        }

        else if(!locationEnabledFlag){
            // GPS not enabled, hence we are setting current location as some
            // default latlng

            locationPickerView.setInitialPosition(ZipRunApp.Constants.DEFAULT_CAMERA_POSITION);

        }
        else if(currentLocation != null) {
            //Current Position Found. Setting it as sourceLocation
            Log.i(TAG, "Move Camera to Current Location");
            locationPickerView.setInitialPosition(currentLatLng);
            //locationPickerView.moveCameraAndDisableListener(currentLatLng);
        }
    }

    public void enableGeocode(boolean enabled){
        performGeocode = enabled;
    }

    public void onPlaceSelected(Observable<Place> placeObservable){
        placeObservable.subscribe(new Action1<Place>() {
            @Override
            public void call(Place place) {
                performGeocode = false;
                sourceLocation.latLng = place.getLatLng();
                sourceLocation.address = String.format("%s, %s",
                        place.getName(), place.getAddress());
                moveToSourceAddress();


            }
        });
    }

    public void moveToSourceAddress(){
        performGeocode = false;
        locationPickerView.moveCamera(sourceLocation.latLng, false);
        Log.i(TAG, "Source Location Address: " + sourceLocation.address);
        locationPickerView.updateAddress(formatAddressAsHtml(sourceLocation.address));
        locationPickerView.enableCameraListener(true);
        performGeocode = true;
    }

    public void moveToCurrentPosition(){
        performGeocode = true;
        locationPickerView.moveCamera(currentLatLng, true);
    }

    public void onCameraChanged(LatLng camPos){
        Log.i(TAG, "Camera Changed. Update Source Location " + camPos.toString()
                + " "  + performGeocode + "  " + sourceLocation.latLng );

        if(shouldDoGeocoding(camPos)) {
            Log.i(TAG, "Performing Geocoding");
            updateSourceLocation(camPos);
            performReverseGeocode();
        }else{
            Log.i(TAG, "Shouldnt do geocoding, distance is too less");
            updateSourceLocation(camPos);
            if(sourceLocation.address != null ){
                locationPickerView.updateAddress(formatAddressAsHtml(sourceLocation.address));
            }
            performGeocode = true;
        }
    }

    private void updateSourceLocation(LatLng pos) {
        sourceLocation.latLng = pos;
        bus.post(new UpdateBookingEvent(booking));
    }

    private boolean shouldDoGeocoding(LatLng pos){
        if(!performGeocode)
            return  false;
        else if(sourceLocation.latLng != null &&
                sourceLocation.address != null) {

            float meters = utils.calculateDistance
                    (sourceLocation.latLng, pos);

            Log.i(TAG, "Ditance btn point in meters " + meters);


            return utils.calculateDistance(sourceLocation.latLng, pos) > 10;
        }
        return true;
    }

    private void performReverseGeocode(){
        locationPickerView.startGeocoding();
        if(geocodeSubscription != null){
            compositeSubscription.remove(geocodeSubscription);
        }
        geocodeSubscription = locationProvider
            .getGeocodeObservable(sourceLocation.latLng.latitude,
                    sourceLocation.latLng.longitude, 1)
            .retryWhen(new RetryWithDelay(3, 2000))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Address>>() {
                @Override
                public void call(List<Address> addresses) {

                    if (addresses.size() == 0)
                        return;

                    sourceLocation.address = utils.addressToString(
                            addresses.get(0), ", ");

                    String address = formatAddressAsHtml(sourceLocation.address);
                    Log.i(TAG, "Reverse Geocoded Address " + address);
                    locationPickerView.updateAddress(address);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage(), throwable);
                    locationPickerView.updateAddress(null);
                    sourceLocation.address = null;
                }
            });

        compositeSubscription.add(geocodeSubscription);
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public String formatAddressAsHtml(String address) {
        String[] addComps = address.split(", ");
        List<String> formattedAddress = new ArrayList<>();

        for (int i = addComps.length - 1; i >= 0; i = i - 2) {
            if (i > 0)
                formattedAddress.add(String.format("%s, %s",
                        addComps[i - 1], addComps[i]));
            else {
                formattedAddress.add(addComps[i]);
            }
        }
        Collections.reverse(formattedAddress);
        return TextUtils.join("<br/>", formattedAddress);
    }

    public void moveForward() {
        bus.post(new OnSourceLocationSet());
    }
}

