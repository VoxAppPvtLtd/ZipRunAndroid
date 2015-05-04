package com.ziprun.consumer.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
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
import com.squareup.otto.Subscribe;
import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.RideType;
import com.ziprun.consumer.event.BookingSubmissionStatus;
import com.ziprun.consumer.event.OnEstimateCalculationFailure;
import com.ziprun.consumer.presenter.ConfirmationPresenter;
import com.ziprun.consumer.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

    @InjectView(R.id.slidingPanel)
    ViewGroup estimateContainer;

    @InjectView(R.id.estimate_cost_container)
    ViewGroup estimateCostContainer;

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

    @InjectView(R.id.txt_source_prefix)
    TextView sourcePrefix;

    @InjectView(R.id.txt_dest_prefix)
    TextView destinationPrefix;

    @InjectView(R.id.review_notes)
    EditText instructions;

    ProgressDialog directionProgress;

    ProgressDialog submitBookingProgress;

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
        setupBookingView();

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
        directionProgress.dismiss();
    }

    @Override
    public void onDestroy() {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mapView != null)
            mapView.onSaveInstanceState(outState);
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

    public void showDirectionProgress() {
        if(!confirmationPresenter.hasDirections()){
            Log.i(TAG, "Show Dialog");
            directionProgress = ProgressDialog.show(getActivity(),
                    getString(R.string.title_dialog_fetching_directions),
                    getString(R.string.msg_dialog_fetching_direction), true);
        }
    }

    public void showBookingSubmissionProgess(){
        submitBookingProgress =  ProgressDialog.show(getActivity(),
            getString(R.string.title_dialog_submit_booking),
            getString(R.string.msg_dialog_submit_booking), true);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected Object getCurrentModule() {
        return new ConfirmationModule(this);
    }

    private void setupBookingView() {
        instructions.setText(confirmationPresenter.getInstruction());

        sourcePrefix.setText(
                confirmationPresenter.getBookingType() == RideType.BUY
                        ? getString(R.string.txt_buy_from)
                        : getString(R.string.txt_pickup_from));

        String srcAddress =  utils.formatAddressAsHtml
                (confirmationPresenter.getSourceAddress());

        String destAddress = utils.formatAddressAsHtml(
                confirmationPresenter.getDestinationAddress());

        sourceAddress.setText(Html.fromHtml(srcAddress));

        destinationAddress.setText(Html.fromHtml(destAddress));
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

    public void showEstimates() {
        directionProgress.dismiss();

        setEstimateBlockVisibility(View.VISIBLE);

        txtEstimateDistance.setText(
            String.format(getString(R.string.txt_estimate_distance),
                (int)confirmationPresenter.getEstimateDistance()));

        estimateCost.setText(String.format("%.2f",
                confirmationPresenter.getEstimatedCost()));

        txtTransactionCharge.setText(
                String.format(getString(R.string.txt_transaction_cost),
                        confirmationPresenter.getTransactionCost()));

        showSlidingPanel();
    }

    private void setEstimateBlockVisibility(int visibility){
        estimateCostContainer.setVisibility(visibility);
        txtTransactionCharge.setVisibility(visibility);

    }

    private void showSlidingPanel(){
        estimateContainer.setVisibility(View.VISIBLE);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public String getInstruction() {
        return instructions.getText().toString();
    }


    @OnClick(R.id.nextBtn)
    public void onNextClicked(View view) {
        confirmationPresenter.submitBooking();
    }

    @Subscribe
    public void estimateCalculationFailed(OnEstimateCalculationFailure event){
        directionProgress.dismiss();
        setEstimateBlockVisibility(View.GONE);
        txtEstimateDistance.setText(R.string.msg_error_estimate);
        showSlidingPanel();
    }


    @Subscribe
    public void onBookingSubmission(BookingSubmissionStatus status){
        submitBookingProgress.dismiss();
        if(status.success){
            Dialog dialog = new Dialog(getActivity(),
                    getString(R.string.title_dialog_confirm_booking),
                    getString(R.string.msg_dialog_confirm_booking));

            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationPresenter.moveForward();
                }
            });

            dialog.show();
        }else{
            Toast.makeText(getActivity(),
                    R.string.error_submit_booking, Toast.LENGTH_LONG).show();
        }
    }


    public void dismissSubmitBookingDialog() {
        if(directionProgress != null)
            directionProgress.dismiss();
    }


    public void dismissDirectionFetchingDialog() {
        if(submitBookingProgress != null)
            submitBookingProgress.dismiss();
    }
}
