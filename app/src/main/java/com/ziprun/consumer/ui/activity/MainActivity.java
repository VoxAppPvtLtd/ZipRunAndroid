package com.ziprun.consumer.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.ziprun.consumer.R;
import com.ziprun.consumer.utils.ClearableAutoCompleteTextView;
import com.ziprun.consumer.utils.PlaceAutocompleteAdapter;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;


public class MainActivity extends ZipBaseActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraChangeListener, ToolTipView.OnToolTipViewClickedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mPickupAdapter;

    private ClearableAutoCompleteTextView mPickupView;

    private GoogleMap mGoogleMap;

    private LatLng pickupLocation;

    private static final LatLngBounds DELHI_BOUNDS = new LatLngBounds(
            new LatLng(28.401067, 28.401067), new LatLng(28.889816, 77.341815));


    private LatLng latlng1 = new LatLng(28.401067, 28.401067);
    private LatLng latlng2 = new LatLng(28.889816, 77.341815);

    private static String API_KEY = "AIzaSyBc58zTmjGgsLR2N4RDkjiTN5HgBlwHUJo";


    @Inject
    Utils utils;
    private ToolTipView myToolTipView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);


//        GoogleDirectionAPI directionAPI = new GoogleDirectionAPI(API_KEY,
//                "Surat, Gujarat", "Delhi" +
//                "");
//
//
//        directionAPI.getDirections().subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(new Action1<Directions>() {
//                   @Override
//                   public void call(Directions directions) {
//                    Log.i(TAG, "Got Directions " + API_KEY);
//                    if(directions.status == Directions.StatusCode.OK){
//                        directions.showRoute(0, mGoogleMap, Color.RED);
//                    }
//
//                   }
//               }, new Action1<Throwable>() {
//                   @Override
//                   public void call(Throwable throwable) {
//                       Log.e(TAG, throwable.getMessage(), throwable);
//                   }
//               }
//            );

        mPickupView = (ClearableAutoCompleteTextView)
                findViewById(R.id.pickup_location);

        mPickupAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                DELHI_BOUNDS, null);

        mPickupView.setAdapter(mPickupAdapter);

        mPickupView.setOnItemClickListener(mPlaceSelector);


        mapFragment.getMapAsync(this);

        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout)
                findViewById(R.id.tooltipView);

        ToolTip toolTip = new ToolTip()
                .withText("A beautiful View")
                .withColor(Color.RED)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.map_marker));
        myToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
    }

    private AdapterView.OnItemClickListener mPlaceSelector
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            PlaceAutocompleteAdapter adapter = (PlaceAutocompleteAdapter) parent.getAdapter();

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = adapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                   .setResultCallback(mplaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mplaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            Log.i(TAG, "Reaching Here Inside callback");
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
            }
            // Get the Place object from the buffer.
            Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getLatLng());

            pickupLocation = place.getLatLng();

            places.release();

        }
    };


    /**
     * Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
     * functionality.
     * This automatically sets up the API client to handle Activity lifecycle events.
     */
    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng delhi = new LatLng(28.586086, 77.171541);

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(delhi, 13));
        mGoogleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mPickupAdapter.setGoogleApiClient(null);

    }


    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mPickupAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "GoogleApiClient connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        //        mPickupAdapter.setGoogleApiClient(null);
        Log.e(TAG, "GoogleApiClient connection suspended.");
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng markerPos = cameraPosition.target;
//        mPickupView.setText("Fetching Target");
//        Log.i(TAG, "New Marker " + markerPos.toString());
//        Geocoder geocoder = new Geocoder(this);
//        try {
//            Observable.from(geocoder.getFromLocation(markerPos.latitude,
//                    markerPos.longitude, 1))
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Address>() {
//                        @Override
//                        public void call(Address address) {
//                            Log.i(TAG, "Address Geocoded " + utils
//                                    .addressToString(address) + " " + address.toString());
//
//                            mPickupView.setText(utils.addressToString(address));
//
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            Log.e(TAG, "Error while geocoding: " + throwable.getMessage(),
//                                    throwable);
//                        }
//                    });
//        } catch (IOException e) {
//            Log.e(TAG, "", e);
//        }
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {

    }
}
