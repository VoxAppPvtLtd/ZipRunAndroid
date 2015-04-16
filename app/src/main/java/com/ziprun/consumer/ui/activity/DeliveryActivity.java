package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.event.OnBookingInstructionSet;
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.InstructionFragment;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;
import com.ziprun.consumer.ui.fragment.SummaryFragment;
import com.ziprun.consumer.ui.fragment.ZipBaseFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeliveryActivity extends ZipBaseActivity {

    private static final String TAG = DeliveryActivity.class.getCanonicalName();

    public static final String KEY_BOOKING = "booking";

    private LocationPickerFragment locationPickerFragment;

    private InstructionFragment instructionFragment;

    private SummaryFragment summaryFragment;

    private ArrayList<String> fragmentTags = new ArrayList<>();

    private FragmentManager fragmentManager;

    private Booking booking;

    @InjectView(R.id.action_bar)
    Toolbar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.inject(this);
        setSupportActionBar(actionBar);

        fragmentManager = getSupportFragmentManager();

        locationPickerFragment = new LocationPickerFragment();

        instructionFragment = new InstructionFragment();

        summaryFragment = new SummaryFragment();

        if(savedInstanceState != null){
            booking = Booking.fromJson(savedInstanceState.getString
                    (KEY_BOOKING));
        }else{
            booking = new Booking();
        }

        locationPickerFragment.setArguments(getBookingBundle());
        fragmentManager.beginTransaction()
                .add(R.id.container, locationPickerFragment,
                        locationPickerFragment.getClass().getSimpleName())
                .commit();

        fragmentTags.add(locationPickerFragment.getClass().getSimpleName());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_BOOKING, booking.toJson());
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

    @Subscribe
    public void onBookingUpdated(UpdateBookingEvent ev){
        booking = ev.booking;
    }

    @Subscribe
    public void onSourceLocationSet(OnSourceLocationSet event){
        moveToFragment(instructionFragment);
    }

    @Subscribe
    public void onBookingInstructionSet(OnBookingInstructionSet event){
        moveToFragment(instructionFragment);
    }


    private Bundle getBookingBundle(){
        Bundle args = new Bundle();
        args.putString(KEY_BOOKING, booking.toJson());
        return args;
    }

    private void moveToFragment(ZipBaseFragment fragment){
        fragment.setArguments(getBookingBundle());
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        fragmentTags.add(fragment.getClass().getSimpleName());

        Log.i(TAG, "Back Stack: " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    public void onBackPressed() {
        fragmentTags.remove(fragmentTags.size() - 1);
        if (fragmentManager.getBackStackEntryCount() > 0){
            Fragment fragment = getFragmentFromBackStack();
            fragment.getArguments().putString(KEY_BOOKING, booking.toJson());
            Log.i(TAG, "Booking " + booking.toJson());
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();

        }else {
            super.onBackPressed();
        }
    }

    private Fragment getFragmentFromBackStack(){
        if(fragmentTags.size() > 0){
            return fragmentManager.findFragmentByTag(fragmentTags.get
                    (fragmentTags.size() - 1));
        }
        return null;
    }
}
