package com.ziprun.consumer.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        presenter.initialize();
        presenter.setBooking(args.getString(DeliveryActivity.KEY_BOOKING));

    }

    @Override
    protected Object[] getModules() {
        ArrayList<Object> modules = new ArrayList<>(
                Arrays.asList(super.getModules()));

        modules.add(getCurrentModule());

        return modules.toArray();
    }

    protected abstract Object getCurrentModule();

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
