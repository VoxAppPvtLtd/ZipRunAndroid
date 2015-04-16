package com.ziprun.consumer.ui.fragment;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.presenter.SourceLocationPresenter;
import com.ziprun.consumer.ui.activity.DeliveryActivity;
import com.ziprun.consumer.ui.custom.AddressAutocompleteView;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

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


    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

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

    @InjectView(R.id.gecode_progress_wheel)
    ProgressWheel geocodeProgessWheel;

    @InjectView(R.id.pickup_location_help_text)
    TextView helpText;

    @InjectView(R.id.closeAddressBtn)
    ImageView closeAddresBtn;

    @Inject
    ReactiveLocationProvider locationProvider;

    @Inject
    SourceLocationPresenter presenter;

    @Inject
    Utils utils;

    private GoogleMap googleMap;

    private Marker currentLocationMarker;

    private boolean inSearchMode = false;

    private boolean slidingPanelExpanded = false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_picker,
                container, false);


        ButterKnife.inject(this, view);

        searchLocBtn.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.white));

        currentLocationBtn.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.button_float_color));


        mapView.onCreate(null);

        setupSlidingLayout();

//        debugContainer();

        return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        presenter.initialize();
        presenter.setBooking(args.getString(DeliveryActivity.KEY_BOOKING));
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
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
        presenter.stop();
        addressAutocompleteView.setOnAddressSelectedListener(null);
        if(googleMap != null)
            googleMap.clear();
        currentLocationMarker = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
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
        presenter.onMapReady();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        presenter.onCameraChanged(cameraPosition.target);
    }

    @OnClick(R.id.searchBtn)
    public void searchAddress(View view) {
        addressAutocompleteView.setVisibility(View.VISIBLE);
        inSearchMode = true;
    }

    @OnClick(R.id.currentLocationBtn)
    public void moveToCurrentLocation(View view){
        presenter.moveToCurrentPosition();

    }

    @OnClick(R.id.closeAddressBtn)
    public void closeAddressArea(View view) {
        hideStaticAddressView();
    }

    @OnClick(R.id.nextBtn)
    public void onNextBtnClicked(View view){
        bus.post(new OnSourceLocationSet());
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
    public void setActionBar(ActionBar actionBar){
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ziprun_white_emboss);
    }


    private void setupSlidingLayout() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingLayout.setTouchEnabled(false);
        slidingLayout.setEnableDragViewTouchEvents(false);
        staticAddressView.setVisibility(View.GONE);
    }


    @Override
    public void onAddressSelected(Observable<Place> place) {
        addressAutocompleteView.setVisibility(View.GONE);
        addressAutocompleteView.reset();
        inSearchMode = false;
        presenter.onPlaceSelected(place);

    }

    public void showAddressView(){
        staticAddressView.setVisibility(View.VISIBLE);
        slidingPanelExpanded = true;
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
//                debugContainer();

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
        helpText.setText(R.string.pickup_location);

        if(address == null){
            Toast.makeText(getActivity(), "Unable to fetch address. Please " +
                            "Check Internet Connectivity or try again later",
                    Toast.LENGTH_LONG).show();

            addressView.setText("Address Not Found");

            return;
        }

        addressView.setText(Html.fromHtml(address));
    }


    public void startResolutionActivity(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_LOCATION_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to resolve status error", e);
            presenter.enableLocationFlag(false);
        }

    }

    public void checkLocationSettings(){
        presenter.checkLocationSettings();
    }

    public void enableLocationFlag(boolean flag){
        presenter.enableLocationFlag(flag);
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
        presenter.enableGeocode(false);
        moveCamera(pos, true, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i(TAG, "Camera Reached " + pos.toString());
                presenter.enableGeocode(true);
                googleMap.setOnCameraChangeListener(LocationPickerFragment.this);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Camera Cancelled " + pos.toString());
                presenter.enableGeocode(true);
                googleMap.setOnCameraChangeListener(LocationPickerFragment.this);
            }
        });
    }

    public void moveCamera(LatLng sourceLocation) {
        moveCamera(sourceLocation, false, null);
    }

    public void moveCamera(LatLng sourceLocation, boolean animate) {
        moveCamera(sourceLocation, animate, null);
    }

    public void moveCamera(LatLng sourceLocation, boolean animate,
                           GoogleMap.CancelableCallback callback) {
        if(animate){
            if(callback != null)
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLng(sourceLocation), callback);
            else
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLng(sourceLocation));

        }else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
        }

    }

    public void startGeocoding() {
        geocodeProgessWheel.setVisibility(View.VISIBLE);
        helpText.setText(R.string.fetching_address);
    }
}
