package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.AddressLocationPair;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.event.OnBookingInstructionSet;
import com.ziprun.consumer.event.OnConfirmBooking;
import com.ziprun.consumer.event.OnDestinationSet;
import com.ziprun.consumer.event.OnSourceLocationSet;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.BackHandlerFragment;
import com.ziprun.consumer.ui.fragment.ConfirmationFragment;
import com.ziprun.consumer.ui.fragment.DestinationLocationPickerFragment;
import com.ziprun.consumer.ui.fragment.InstructionFragment;
import com.ziprun.consumer.ui.fragment.SourceLocationPickerFragment;
import com.ziprun.consumer.ui.fragment.SummaryFragment;
import com.ziprun.consumer.ui.fragment.ZipBaseFragment;

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

    private FragmentManager fragmentManager;

    private Booking booking;

    private int currentLeg;

    @InjectView(R.id.action_bar)
    Toolbar actionBar;

    @InjectView(R.id.ziprun_icon)
    ImageView ziprunIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.inject(this);
        try {
            setSupportActionBar(actionBar);
        }catch (Throwable t){
            Log.e(TAG, "Fucking Samsung sort your issues out");
        }

        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState != null){
            booking = Booking.fromJson(
                    savedInstanceState.getString(KEY_BOOKING), Booking.class);
            currentLeg = savedInstanceState.getInt(KEY_CURRENT_LEG);
            return;
        }

        SourceLocationPickerFragment sourcePickerFragment =
                new SourceLocationPickerFragment();

        booking = new Booking();
        currentLeg = 0;
        booking.addBookingLeg(new AddressLocationPair());

        Bundle args = getBookingBundle();
        sourcePickerFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.container, sourcePickerFragment,
                        getFragmentTag(sourcePickerFragment))
                .commit();
    }


    private String getFragmentTag(ZipBaseFragment fragment){
        return String.format("%s:%d", fragment.getClass().getSimpleName(),
                currentLeg);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_BOOKING, booking.toJson());
        outState.putInt(KEY_CURRENT_LEG, currentLeg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            Fragment fragment = getFragmentFromBackStack();
            if(fragment == null){
                super.onBackPressed();
                return;
            }

            Log.i(TAG, "Fragment: " +  fragment.getClass().getSimpleName() +
                    " Booking " + booking.toJson() );

            fragment.getArguments().putString(KEY_BOOKING, booking.toJson());
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();

        }else {
            Log.i(TAG, "Nothing on the backstack");
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onBookingUpdated(UpdateBookingEvent ev){
        booking = ev.booking;
    }

    @Subscribe
    public void onSourceLocationSet(OnSourceLocationSet event){
        moveToFragment(new InstructionFragment());
    }

    @Subscribe
    public void onBookingInstructionSet(OnBookingInstructionSet event){
        //sourcePickerFragment.getArguments().putString(KEY_BOOKING,
          //      booking.toJson());
        moveToFragment(new DestinationLocationPickerFragment());
    }

    @Subscribe
    public void onDestinationLocationSet(OnDestinationSet event){
        moveToFragment(new ConfirmationFragment());
    }

    @Subscribe
    public void onConfirmBooking(OnConfirmBooking event){
        ziprunIcon.setImageResource(R.drawable.ziprun_toolbar_icon_motion);
        clearBackStack();
        moveToFragment(new SummaryFragment(), false);

    }

    private Bundle getBookingBundle(){
        Bundle args = new Bundle();
        args.putString(KEY_BOOKING, booking.toJson());

        args.putInt(KEY_CURRENT_LEG, currentLeg);
        return args;
    }

    private void moveToFragment(ZipBaseFragment fragment){
        moveToFragment(fragment, true);
    }


    private void moveToFragment(ZipBaseFragment fragment,
                               boolean addToBackStack){

        fragment.setArguments(getBookingBundle());
        FragmentTransaction transaction =
                fragmentManager.beginTransaction()
                               .replace(R.id.container, fragment,
                                       getFragmentTag(fragment));

        if(addToBackStack)
            transaction.addToBackStack(currentFragment.getTag());

        transaction.commit();

    }

    @Override
    public void setSelectedFragment(BackHandlerFragment selectedFragment) {
        currentFragment = selectedFragment;
    }

    public Fragment getFragmentFromBackStack() {
        FragmentManager.BackStackEntry backEntry = fragmentManager
            .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

        Log.i(TAG, "Back Entry Name " + backEntry.getName());

        return fragmentManager.findFragmentByTag(backEntry.getName());
    }

    private void clearBackStack() {
//
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
//            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
//
//        Log.i(TAG, "BackStack Count " + fragmentManager
//                .getBackStackEntryCount());

        fragmentManager.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

}
