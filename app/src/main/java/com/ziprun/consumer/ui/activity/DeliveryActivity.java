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
import com.ziprun.consumer.data.model.AddressLocationPair;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.event.OnBookingInstructionSet;
import com.ziprun.consumer.event.OnDestinationSet;
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.BackHandlerFragment;
import com.ziprun.consumer.ui.fragment.ConfirmationFragment;
import com.ziprun.consumer.ui.fragment.DestinationLocationPickerFragment;
import com.ziprun.consumer.ui.fragment.InstructionFragment;
import com.ziprun.consumer.ui.fragment.SourceLocationPickerFragment;
import com.ziprun.consumer.ui.fragment.ZipBaseFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeliveryActivity extends ZipBaseActivity implements
        BackHandlerFragment.BackHandlerInterface {

    private static final String TAG = DeliveryActivity.class.getCanonicalName();

    public static final String KEY_BOOKING = "booking";
    private static final String GOOGLE_CONNECTION_ERROR = "connection_error";
    public static final String KEY_LOCATION_TYPE = "locationType";
    public static final String KEY_CURRENT_LEG = "current_leg";

    protected BackHandlerFragment currentFragment;

    private SourceLocationPickerFragment sourcePickerFragment;

    private InstructionFragment instructionFragment;

    private DestinationLocationPickerFragment destPickerFrament;

    private ConfirmationFragment confirmationFragment;

    private ArrayList<String> fragmentTags = new ArrayList<>();

    private FragmentManager fragmentManager;

    private Booking booking;

    private int currentLeg;

    @InjectView(R.id.action_bar)
    Toolbar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.inject(this);
        setSupportActionBar(actionBar);

        fragmentManager = getSupportFragmentManager();

        sourcePickerFragment = new SourceLocationPickerFragment();

        instructionFragment = new InstructionFragment();

        confirmationFragment = new ConfirmationFragment();

        destPickerFrament = new DestinationLocationPickerFragment();

        if(savedInstanceState != null){
            booking = Booking.fromJson(savedInstanceState.getString
                    (KEY_BOOKING));

        }else{
            booking = new Booking();
            booking.addBookingLeg(new AddressLocationPair());
        }

        currentLeg = 0;

        Bundle args = getBookingBundle();

        sourcePickerFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.container, sourcePickerFragment,
                        sourcePickerFragment.getClass().getSimpleName())
                .commit();

        fragmentTags.add(sourcePickerFragment.getClass().getSimpleName());
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
                onBackPressed();

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentFragment.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment != null && currentFragment.onBackPressed()){
            return;
        }

        else if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentTags.remove(fragmentTags.size() - 1);
            Fragment fragment = getFragmentFromBackStack();
            fragment.getArguments().putString(KEY_BOOKING, booking.toJson());
            Log.i(TAG, "Booking " + booking.toJson());
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();

        }else {
            super.onBackPressed();
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
        //sourcePickerFragment.getArguments().putString(KEY_BOOKING,
          //      booking.toJson());
        moveToFragment(destPickerFrament);
    }

    @Subscribe
    public void onDestinationLocationSet(OnDestinationSet event){
        moveToFragment(confirmationFragment);
    }


    private Bundle getBookingBundle(){
        Bundle args = new Bundle();
        args.putString(KEY_BOOKING, booking.toJson());

        args.putInt(KEY_CURRENT_LEG, currentLeg);
        return args;
    }

    private void moveToFragment(ZipBaseFragment fragment){
        fragment.setArguments(getBookingBundle());
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        fragmentTags.add(fragment.getClass().getSimpleName());

    }

    @Override
    public void setSelectedFragment(BackHandlerFragment selectedFragment) {
        currentFragment = selectedFragment;
    }


    private Fragment getFragmentFromBackStack(){
        if(fragmentTags.size() > 0){
            return fragmentManager.findFragmentByTag(fragmentTags.get
                    (fragmentTags.size() - 1));
        }
        return null;
    }

}
