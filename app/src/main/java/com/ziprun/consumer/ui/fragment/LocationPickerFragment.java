package com.ziprun.consumer.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ziprun.consumer.R;
import com.ziprun.consumer.presenter.LocationPickerPresenter;
import com.ziprun.consumer.ui.custom.AddressAutocompleteView;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public abstract class LocationPickerFragment extends DeliveryFragment implements
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

    private static final int REQUEST_FIX_GOOGLE_API_ERROR = 1;
    private static final String DIALOG_ERROR = "Dialog Error";


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @InjectView(R.id.slidingLayout  )
    SlidingUpPanelLayout slidingLayout;

    @InjectView(R.id.map)
    MapView mapView;

    @InjectView(R.id.nextBtn)
    Button nextBtn;

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

    @InjectView(R.id.gecode_progress_wheel)
    ProgressWheel geocodeProgessWheel;

    @InjectView(R.id.help_text)
    TextView helpText;

    @InjectView(R.id.closeAddressBtn)
    ImageView closeAddresBtn;

    @Inject
    ReactiveLocationProvider locationProvider;

    @Inject
    Utils utils;

    protected LocationPickerPresenter locationPickerPresenter;

    private GoogleMap googleMap;

    private Marker currentLocationMarker;

    private boolean inSearchMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "On Create View Called");

        View view = inflater.inflate(R.layout.fragment_location_picker,
                        container, false);

        ButterKnife.inject(this, view);

        mapView.onCreate(null);
        searchLocBtn.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.white));

        currentLocationBtn.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.button_float_color));

        setupSlidingLayout();

        addressAutocompleteView.setVisibility(View.GONE);
//        debugContainer();

        return view;
    }

    public abstract int getMarkerResource();
    public abstract int getNextBtnResource();

    @Override
    protected void processArguments(Bundle args) {
        super.processArguments(args);
        locationPickerPresenter = (LocationPickerPresenter)presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        nextBtn.setText(getNextBtnResource());
        mapMarker.setImageResource(getMarkerResource());
        helpText.setText(getHelpTextResource());

        mapView.getMapAsync(this);
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
        locationPickerPresenter.stop();
        addressAutocompleteView.setOnAddressSelectedListener(null);
        if(googleMap != null)
            googleMap.clear();
        currentLocationMarker = null;
        googleMap = null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Location Picker Fragment is destroyed");
        super.onDestroy();
        if(mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(this.getActivity());
        this.googleMap.setMyLocationEnabled(false);
        locationPickerPresenter.onMapReady();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        locationPickerPresenter.onCameraChanged(cameraPosition.target);
    }

    @OnClick(R.id.searchBtn)
    public void searchAddress(View view) {
        addressAutocompleteView.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.GONE);

        hideStaticAddressView();
        inSearchMode = true;
    }

    @Override
    public boolean onBackPressed() {
        if(inSearchMode){
            inSearchMode = false;
            addressAutocompleteView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode,
                                           int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_LOCATION_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPickerPresenter.checkLocationSettings();
            }else{
                locationPickerPresenter.enableLocationFlag(false);
            }
            return true;
        }else if(requestCode == REQUEST_FIX_GOOGLE_API_ERROR){
            if(resultCode == Activity.RESULT_OK){
                locationPickerPresenter.checkLocationSettings();
                locationPickerPresenter.setUpLocationUpdates();
            }else{
                locationPickerPresenter.enableLocationFlag(false);
            }

        }
        return false;
    }

    @OnClick(R.id.currentLocationBtn)
    public void moveToCurrentLocation(View view){
        locationPickerPresenter.moveToCurrentPosition();

    }

    @OnClick(R.id.closeAddressBtn)
    public void closeAddressArea(View view) {
        hideStaticAddressView();
    }

    @OnClick(R.id.nextBtn)
    public void onNextBtnClicked(View view){
        locationPickerPresenter.moveForward();
    }

    private void setupSlidingLayout() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingLayout.setTouchEnabled(false);
        slidingLayout.setEnableDragViewTouchEvents(false);
        slidingLayout.setPanelHeight(0);
        staticAddressView.setVisibility(View.GONE);
    }


    @Override
    public void onAddressSelected(Observable<Place> place) {
        mapView.setVisibility(View.VISIBLE);
        addressAutocompleteView.setVisibility(View.GONE);
        addressAutocompleteView.reset();
        inSearchMode = false;
        locationPickerPresenter.onPlaceSelected(place);

    }

    public void showAddressView(){
        Log.i(TAG, "Show Address View Called");
        staticAddressView.setVisibility(View.VISIBLE);
        final int origHeight = mapContainer.getHeight();
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float offset) {
            }

            @Override
            public void onPanelCollapsed(View view) {
            }

            @Override
            public void onPanelExpanded(View view) {
                ViewGroup.LayoutParams lp =
                        (ViewGroup.LayoutParams)mapContainer.getLayoutParams();

                lp.height = origHeight - view.getHeight();

                Log.i(TAG," On Panel Expanded" +  origHeight + " " + view
                        .getHeight() + " " + mapContainer.getHeight());

                mapContainer.setLayoutParams(lp);

                mapContainer.requestLayout();
////                debugContainer();
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
                view.setVisibility(View.GONE);
            }
        });

    }

//    private void debugContainer(){
//        ViewTreeObserver vto = mapContainer.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mapContainer.getViewTreeObserver().removeGlobalOnLayoutListener
//                        (this);
//                int height = mapContainer.getMeasuredHeight();
//
//                Log.i(TAG, "Map Container Height " + height );
//                Log.i(TAG, "Current Position Top " +
//                        currentLocationBtn.getTop());
//                Log.i(TAG, "Map Container Top " + mapContainer.getTop());
//
//                Log.i(TAG, "Search Btn Position Top " +
//                        searchLocBtn.getTop());
//
//
//            }
//        });
//    }

    public void hideStaticAddressView(){
        staticAddressView.setVisibility(View.GONE);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void updateAddress(String address){
        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN)
            showAddressView();

        geocodeProgessWheel.setVisibility(View.GONE);
        helpText.setText(getHelpTextResource());

        if(address == null){
            Toast.makeText(getActivity(), "Unable to fetch address. Please " +
                            "Check Internet Connectivity or try again later",
                    Toast.LENGTH_LONG).show();

            addressView.setText("Address Not Found");

            return;
        }

        addressView.setText(Html.fromHtml(address));
    }


    public void enableLocationServices(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_LOCATION_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to resolve status error", e);
            locationPickerPresenter.enableLocationFlag(false);
        }
    }


    public void checkLocationSettings(){
        locationPickerPresenter.checkLocationSettings();
    }

    public void enableLocationFlag(boolean flag){
        locationPickerPresenter.enableLocationFlag(flag);
    }

    public void setCurrentLocationMarker(LatLng currentLocation) {
        if(currentLocationMarker == null){
            currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_red_dot)));
        }else{
            currentLocationMarker.setPosition(currentLocation);
        }
    }

    public void showCurrentLocationBtn(boolean enabled) {
        currentLocationBtn.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void enableCameraListener(boolean enabled){
        googleMap.setOnCameraChangeListener(enabled ? this : null);
    }

    public void setInitialPosition(LatLng pos){

        moveCamera(pos);
        enableCameraListener(true);
    }

    public void moveCameraAndDisableListener(final LatLng pos){
        googleMap.setOnCameraChangeListener(null);
        locationPickerPresenter.enableGeocode(false);
        moveCamera(pos, true, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i(TAG, "Camera Reached " + pos.toString());
                locationPickerPresenter.enableGeocode(true);
                googleMap.setOnCameraChangeListener(LocationPickerFragment.this);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Camera Cancelled " + pos.toString());
                locationPickerPresenter.enableGeocode(true);
                googleMap.setOnCameraChangeListener(LocationPickerFragment.this);
            }
        });
    }

    public void moveCamera(LatLng latlng) {
        moveCamera(latlng, false, null);
    }

    public void moveCamera(LatLng latlng, boolean animate) {
        moveCamera(latlng, animate, null);
    }

    public void moveCamera(LatLng latlng, boolean animate,
                           GoogleMap.CancelableCallback callback) {
        if(animate){
            if(callback != null)
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLng(latlng), callback);
            else
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLng(latlng));

        }else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }

    }

    public void startGeocoding() {
        geocodeProgessWheel.setVisibility(View.VISIBLE);
        helpText.setText(R.string.fetching_address);
    }

    public abstract int getHelpTextResource();
}
