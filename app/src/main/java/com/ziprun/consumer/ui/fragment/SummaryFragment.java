package com.ziprun.consumer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ziprun.consumer.R;
import com.ziprun.consumer.utils.Utils;
import com.ziprun.maputils.GoogleDirectionAPI;
import com.ziprun.maputils.models.Directions;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SummaryFragment extends ZipBaseFragment implements OnMapReadyCallback {
    private static final String TAG = SummaryFragment.class.getCanonicalName();

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @InjectView(R.id.notes_container)
    LinearLayout notesContainer;

    @InjectView(R.id.map)
    MapView mapView;

    @Inject
    Utils utils;

    Subscription directionSubscriber;

    private LatLng latlng1 = new LatLng(21.167790, 72.795143);
    private LatLng latlng2 = new LatLng(21.154042, 72.772831);

    private static String API_KEY = "AIzaSyBc58zTmjGgsLR2N4RDkjiTN5HgBlwHUJo";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_summary,
                container, false);

        ButterKnife.inject(this, view);

        setupSlidingLayout();

        mapView.onCreate(null);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    protected void setActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(R.string.confirm_fragment_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "On Saved Instance State Called");

        super.onSaveInstanceState(outState);
        for (String key: outState.keySet())
        {
            Log.i (TAG, key + " is a key in the bundle: " +  outState.get(key));
        }


        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(directionSubscriber != null && !directionSubscriber.isUnsubscribed()){
            directionSubscriber.unsubscribe();
        }
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
    
    public void setupSlidingLayout() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                notesContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                notesContainer.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
        
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        int activityHeight = utils.getScreenHeight();

        int activityWidth = utils.getScreenWidth();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latlng1);
        builder.include(latlng2);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder
                .build(), activityWidth, activityHeight, 10));

        googleMap.addMarker(new MarkerOptions()
                .position(latlng1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable
                        .icon_blue_map_marker))
                        .anchor(0.5f, 0.7f));

        googleMap.addMarker(new MarkerOptions()
                .position(latlng2)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable
                        .icon_green_map_marker))
                        .anchor(0.5f, 0.8f));

        GoogleDirectionAPI googleDirectionAPI = new GoogleDirectionAPI
                (API_KEY, latlng1, latlng2);


        directionSubscriber = googleDirectionAPI.getDirections()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Directions>() {
                    @Override
                    public void call(Directions directions) {
                        List<LatLng> points = directions.getPoints(0);

                        googleMap.addPolyline(
                                new PolylineOptions().addAll(points))
                                .setColor(getResources().getColor(R.color.route_color));
                    }
                });
    }
}
