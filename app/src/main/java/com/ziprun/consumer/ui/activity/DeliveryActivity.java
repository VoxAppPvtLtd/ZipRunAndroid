package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.ui.fragment.InstructionFragment;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;
import com.ziprun.consumer.ui.fragment.SummaryFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeliveryActivity extends ZipBaseActivity {

    private static final String TAG = DeliveryActivity.class.getCanonicalName();

    public static final String KEY_BOOKING = "booking";

    private LocationPickerFragment locationPickerFragment;

    private InstructionFragment instructionFragment;

    private SummaryFragment summaryFragment;

    private Booking booking;

    @InjectView(R.id.action_bar)
    Toolbar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.inject(this);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ziprun_white_emboss);

        locationPickerFragment = new LocationPickerFragment();

        instructionFragment = new InstructionFragment();

        summaryFragment = new SummaryFragment();

        booking = new Booking();

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            Log.i(TAG, "Booking: " + booking.toJson());
            args.putString(KEY_BOOKING, booking.toJson());
            locationPickerFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, locationPickerFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delivery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case android.R.id.home: {
                Log.i(TAG, "Back Button Pressed in action bar");
            }

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LocationPickerFragment.REQUEST_CHECK_LOCATION_SETTINGS &&
                locationPickerFragment != null){
            Log.i(TAG, "Result Code " + resultCode);
            if(resultCode == RESULT_OK){
                    locationPickerFragment.checkLocationSettings();
            }else{
                Log.i(TAG, "Disabled Location Flag");
                locationPickerFragment.enableLocationFlag(false);
            }
        }


    }
}
