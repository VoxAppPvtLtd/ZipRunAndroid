package com.ziprun.consumer.ui.fragment;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

public class LocationPickerFragment extends ZipBaseFragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {
    private static final String TAG = LocationPickerFragment.class.getSimpleName();

    public static final int REQUEST_CHECK_LOCATION_SETTINGS = 0;

    private static final LatLng DELHI_LATLNG = new LatLng(28.586086, 77.171541);

    private static final LocationRequest locationRequest =
            LocationRequest.create()
                           .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                           .setSmallestDisplacement(10)
                           .setInterval(100);

    @InjectView(R.id.map)
    MapView mapView;

    @InjectView(R.id.searchBtn)
    ImageView searchLocBtn;

    @InjectView(R.id.map_marker)
    ImageView mapMarker;

    @Inject
    ReactiveLocationProvider locationProvider;

    private GoogleMap googleMap;
    private LatLng currentLocation;
    private LatLng markerPosition;

    private Subscription locationUpdateSubcription;

    private Boolean locationEnabledFlag = null;

    private boolean isMapReady =  false;

    private boolean isMapMarkerSet = false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_picker,
                container, false);

        ButterKnife.inject(this, view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpLocationUpdates();

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
            currentLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());

            if(!isMapMarkerSet)
                setMapMarker();
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(this.getActivity());
        isMapReady = true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        this.markerPosition = cameraPosition.target;
        Log.i(TAG, "Marker Position changed: " + cameraPosition.target.toString());
    }

    public void checkLocationSettings() {
        locationProvider.checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .build()
        ).subscribe(new Action1<LocationSettingsResult>() {
            @Override
            public void call(LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                Log.i(TAG, "Location Settings Status " + status
                        .getStatusMessage());

                if(status.isSuccess()){
                    isLocationEnabled(true);
                }
                else if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException th) {
                        Log.e(TAG, "Error opening settings activity.", th);
                    }
                }else{
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
            googleMap.setMyLocationEnabled(false);
            isMapMarkerSet = true;

        }
        else {
            if(currentLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                isMapMarkerSet = true;
            }
            googleMap.setMyLocationEnabled(true);
        }
    }
}
