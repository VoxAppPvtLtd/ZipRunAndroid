package com.ziprun.consumer.ui.fragment;

import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ziprun.consumer.R;
import com.ziprun.consumer.ui.custom.AddressAutocompleteView;
import com.ziprun.consumer.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LocationPickerFragment extends ZipBaseFragment implements
        OnMapReadyCallback, GoogleMap.OnCameraChangeListener,
        AddressAutocompleteView.OnAddressSelectedListener {
    private static final String TAG = LocationPickerFragment.class.getSimpleName();

    public static final int REQUEST_CHECK_LOCATION_SETTINGS = 0;

    private static final LatLng DELHI_LATLNG = new LatLng(28.586086, 77.171541);

    private static final LocationRequest locationRequest =
            LocationRequest.create()
                           .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                           .setSmallestDisplacement(10)
                           .setInterval(100);

    private static final int STATIC_ADDRESS_VIEW_HEIGHT = 200; //dp

    @InjectView(R.id.map)
    MapView mapView;

    @InjectView(R.id.mapContainer)
    ViewGroup mapContainer;

    @InjectView(R.id.searchBtn)
    ButtonFloat searchLocBtn;

    @InjectView(R.id.currentLocationBtn)
    ButtonFloat currentLocationBtn;

    @InjectView(R.id.map_marker)
    ImageView mapMarker;

    @InjectView(R.id.static_address)
    ViewGroup staticAddressView;

    @InjectView(R.id.address_autocomplete_view)
    AddressAutocompleteView addressAutocompleteView;

    @InjectView(R.id.address)
    TextView addressView;

    @InjectView(R.id.closeAddressBtn)
    ImageView closeAddresBtn;

    @Inject
    ReactiveLocationProvider locationProvider;

    @Inject
    Utils utils;


    private GoogleMap googleMap;
    private Location currentLocation;
    private LatLng markerPosition;

    private Marker currentLocationMarker;

    private boolean firstLoad = true;

    private Subscription locationUpdateSubcription;

    private Boolean locationEnabledFlag = null;

    private boolean isMapReady =  false;

    private boolean isMapMarkerSet = false;

    private boolean inSearchMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_picker,
                container, false);


        ButterKnife.inject(this, view);

        searchLocBtn.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.white));

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpLocationUpdates();
        addressAutocompleteView.setOnAddressSelectedListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationUpdateSubcription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void setUpLocationUpdates(){
        checkLocationSettings();

        locationUpdateSubcription = locationProvider.getLastKnownLocation()
                .concatWith(locationProvider.getUpdatedLocation(locationRequest))
                .subscribe(locationSetter);

    }

    private Action1<Location> locationSetter = new Action1<Location>() {
        @Override
        public void call(Location location) {
            Log.i(TAG, "Location Update " + location.toString());
            currentLocation = location;

            if(!isMapMarkerSet) {
                setMapMarker();
            }else{
                currentLocationMarker.setPosition(
                        utils.getLatLngFromLocation(currentLocation));
            }
        }
    };

    public void checkLocationSettings() {
        locationProvider.checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .build()
        )
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<LocationSettingsResult>() {
            @Override
            public void call(LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                Log.i(TAG, "Location Settings Status " + status
                        .getStatusMessage());

                if (status.isSuccess()) {
                    isLocationEnabled(true);
                } else if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException th) {
                        Log.e(TAG, "Error opening settings activity.", th);
                    }
                } else {
                    Toast.makeText(getActivity(),
                            R.string.check_location_settings_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void isLocationEnabled(Boolean flag) {
        locationEnabledFlag = flag;
        setMapMarker();

    }

    private void setMapMarker() {
        if(!isMapReady || locationEnabledFlag == null)
            return;


        else if(!locationEnabledFlag){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(DELHI_LATLNG));
            isMapMarkerSet = true;
            currentLocationBtn.setVisibility(View.GONE);
        }
        else if(currentLocation != null) {
            currentLocationBtn.setVisibility(View.VISIBLE);
            LatLng latLng = utils.getLatLngFromLocation(currentLocation);
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLng(latLng));
            isMapMarkerSet = true;
            currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_red_dot)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(this.getActivity());
        isMapReady = true;
        this.googleMap.setMyLocationEnabled(false);
        this.googleMap.setOnCameraChangeListener(this);
        setMapMarker();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        this.markerPosition = cameraPosition.target;
        Log.i(TAG, "Camera Changed " + this.markerPosition);
        locationProvider.getGeocodeObservable(this.markerPosition.latitude,
                this.markerPosition.longitude, 1)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Address>>() {
                @Override
                public void call(List<Address> addresses) {
                    if (addresses.size() == 0)
                        return;

                    String address = utils.addressToString(addresses.get(0),
                            "<br/>");
                    Log.i(TAG, "Reverse Geocoded Address " + address);

                    if (firstLoad) {
                        firstLoad = false;
                        return;
                    }

                    if (staticAddressView.getVisibility() == View.GONE)
                        showStaticAddressView();

                    addressView.setText(Html.fromHtml(address));
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage(), throwable);
                }
            });
    }

    @OnClick(R.id.searchBtn)
    public void searchAddress(View view) {
        addressAutocompleteView.setVisibility(View.VISIBLE);
        inSearchMode = true;
    }

    @OnClick(R.id.currentLocationBtn)
    public void moveToCurrentLocation(View view){
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(
                        utils.getLatLngFromLocation(currentLocation)));
    }

    @OnClick(R.id.closeAddressBtn)
    public void closeAddressArea(View view) {
        hideStaticAddressView();
    }

    @Override
    public boolean onBackPressed() {
        if(inSearchMode){
            inSearchMode = false;
            addressAutocompleteView.setVisibility(View.GONE);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onAddressSelected(Observable<Place> place) {
        addressAutocompleteView.setVisibility(View.GONE);
        addressAutocompleteView.reset();
        inSearchMode = false;
        place.subscribe(new Action1<Place>() {
            @Override
            public void call(Place place) {
                Log.i(TAG, "Place Selected : " + place.getAddress
                        ().toString());

                googleMap.animateCamera(CameraUpdateFactory
                        .newLatLng(place.getLatLng()));
            }
        });
    }

    public void showStaticAddressView(){
        int activityHeight = mapContainer.getHeight();

        int newHeight = activityHeight - utils.convertDpToPixel
                (STATIC_ADDRESS_VIEW_HEIGHT, getActivity());

        mapContainer.setLayoutParams(new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, newHeight));

        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                R.anim.bottom_up);

        staticAddressView.startAnimation(bottomUp);
        staticAddressView.setVisibility(View.VISIBLE);
    }

    public void hideStaticAddressView(){

        mapContainer.setLayoutParams(new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Animation bottom_down = AnimationUtils.loadAnimation(getActivity(),
                R.anim.bottom_down);

        staticAddressView.startAnimation(bottom_down);
        staticAddressView.setVisibility(View.GONE);

    }
}
