package com.ziprun.consumer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.ziprun.consumer.presenter.ConfirmationPresenter;
import com.ziprun.consumer.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ConfirmationFragment extends DeliveryFragment implements OnMapReadyCallback {
    private static final String TAG = ConfirmationFragment.class.getCanonicalName();

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @InjectView(R.id.notes_container)
    LinearLayout notesContainer;

    @InjectView(R.id.map)
    MapView mapView;

    @Inject
    Utils utils;

    @InjectView(R.id.estimate_container)
    ViewGroup estimateContainer;

    @InjectView(R.id.txt_estimate_distance)
    TextView txtEstimateDistance;

    @InjectView(R.id.estimate_cost)
    TextView estimateCost;

    @InjectView(R.id.txt_transaction_charge)
    TextView txtTransactionCharge;

    @InjectView(R.id.source_address)
    TextView sourceAddress;

    @InjectView(R.id.dest_address)
    TextView destinationAddress;

    @InjectView(R.id.review_notes)
    EditText instructions;

    ConfirmationPresenter confirmationPresenter;

    GoogleMap googleMap;

    LatLng sourceLatLng;
    LatLng destLatLng;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_confirmation,
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
        sourceLatLng = confirmationPresenter.getSourceLatLng();
        destLatLng = confirmationPresenter.getDestinationLatLng();
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
        googleMap.clear();
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
    protected void processArguments(Bundle args) {
        super.processArguments(args);
        confirmationPresenter = (ConfirmationPresenter) presenter;
    }

    public void setupSlidingLayout() {
        estimateContainer.setVisibility(View.GONE);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
    protected Object getCurrentModule() {
        return new ConfirmationModule(this);
    }


    @Override
    public void onMapReady(final GoogleMap map) {

        Log.i(TAG, "Map is Ready ");
        this.googleMap = map;
        MapsInitializer.initialize(getActivity());

        confirmationPresenter.onMapReady();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(sourceLatLng);
        builder.include(destLatLng);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder
                .build(), utils.getScreenWidth(), utils.getScreenHeight(), 10));

        googleMap.addMarker(new MarkerOptions()
                .position(sourceLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable
                        .icon_blue_map_marker))
                .anchor(0.5f, 0.7f));

        googleMap.addMarker(new MarkerOptions()
                .position(destLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable
                        .icon_green_map_marker))
                .anchor(0.5f, 0.8f));

    }

    public void drawRoute(List<LatLng> routePoints){
        googleMap.addPolyline(new PolylineOptions().addAll(routePoints))
                .setColor(getResources().getColor(R.color.route_color));

    }

    public void showEstimates(int distance, int cost, int transactionCost) {
       
        sourceAddress.setText(utils.formatAddressAsHtml(confirmationPresenter
                .getSourceAddress()));

        destinationAddress.setText(utils.formatAddressAsHtml(confirmationPresenter
                .getDestinationAddress()));

        txtEstimateDistance.setText(String.format(getString(R.string
                .txt_estimate_distance), distance));

        estimateCost.setText(cost + ".00");

        txtTransactionCharge.setText(
                String.format(getString(R.string.txt_transaction_cost), transactionCost));

        estimateContainer.setVisibility(View.VISIBLE);

        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

    }
}
