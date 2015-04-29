package com.ziprun.consumer.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;
import com.ziprun.consumer.R;
import com.ziprun.consumer.event.GoogleConnectionErrorDialogDismissed;
import com.ziprun.consumer.presenter.DeliveryPresenter;
import com.ziprun.consumer.ui.activity.DeliveryActivity;
import com.ziprun.consumer.utils.AndroidBus;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

public abstract  class DeliveryFragment extends ZipBaseFragment  {
    private static final String TAG = DeliveryFragment.class.getCanonicalName();

    private static final String GOOGLE_CONNECTION_ERROR = "connection_error";

    @Inject
    DeliveryPresenter presenter;

    protected boolean resolvingConnectionError = false;

    protected static final int REQUEST_FIX_GOOGLE_API_ERROR = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, this.getClass().getSimpleName() + " Started");
        presenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(presenter != null)
            presenter.destroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            Log.i(TAG, "Saved Instance state is not null");
            processArguments(savedInstanceState);
        }else{
            Log.i(TAG, "Saved Instance state is null");
            processArguments(getArguments());
        }
        setActionBar(activity.getSupportActionBar());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(presenter != null)
            presenter.saveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_delivery, menu);
//        menu.findItem(R.id.ziprun_icon).setIcon(getMenuIcon());
//
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void processArguments(Bundle args) {
        presenter.initialize();
        String bookingJson = args.getString(DeliveryActivity.KEY_BOOKING);
        int currentLeg = args.getInt(DeliveryActivity.KEY_CURRENT_LEG);
        presenter.setBooking(bookingJson, currentLeg);
    }

    @Override
    protected Object[] getModules() {
        ArrayList<Object> modules = new ArrayList<>(
                Arrays.asList(super.getModules()));

        modules.add(getCurrentModule());

        return modules.toArray();
    }

    protected abstract Object getCurrentModule();

    protected abstract void setActionBar(ActionBar supportActionBar);


    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return  false;

    }

    public void resolveGoogleAPIConnectionError(ConnectionResult result) throws
            Exception {
        if (resolvingConnectionError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            resolvingConnectionError = true;
            result.startResolutionForResult(getActivity(),
                    REQUEST_FIX_GOOGLE_API_ERROR);

        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            resolvingConnectionError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(GOOGLE_CONNECTION_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(),
                "Google Connecion Error");
    }

    @Subscribe
    public void onDialogDismissed(GoogleConnectionErrorDialogDismissed event) {
        resolvingConnectionError = false;
    }

    public int getMenuIcon() {
        return R.drawable.ziprun_toolbar_icon;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {

        @Inject
        AndroidBus bus;

        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(GOOGLE_CONNECTION_ERROR);
            ((DeliveryActivity)getActivity()).inject(this);

            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_FIX_GOOGLE_API_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            bus.post(new GoogleConnectionErrorDialogDismissed());
        }
    }

}
