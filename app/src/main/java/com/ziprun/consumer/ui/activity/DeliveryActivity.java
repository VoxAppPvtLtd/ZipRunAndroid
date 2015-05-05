package com.ziprun.consumer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.ziprun.consumer.R;
import com.ziprun.consumer.ZipRunApp;
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
import timber.log.Timber;

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

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.contentView)
    ViewGroup contentView;

    @InjectView(R.id.action_bar)
    Toolbar actionBar;

    @InjectView(R.id.ziprun_icon)
    ImageView ziprunIcon;

    @InjectView(R.id.left_drawer)
    ListView navDrawerList;

    private String[] navItems;

    private ActionBarDrawerToggle drawerToggle;

    private FragmentManager.OnBackStackChangedListener
         backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            syncNavDrawer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.inject(this);
        contentView.requestTransparentRegion(contentView);

        try {
            setSupportActionBar(actionBar);
        }catch (Throwable t){
            Timber.wtf(TAG, "Fucking Samsung sort your issues out");
        }

        setupNavigationDrawer();


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

        fragmentManager.addOnBackStackChangedListener(backStackChangedListener);
    }

    private void setupNavigationDrawer() {
        navItems = getResources().getStringArray(R.array.nav_items);

        navDrawerList.setAdapter(new NavigationDrawerAdapter(this, navItems));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                actionBar, R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);

        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {

                if (position == 0) {
                    if (!currentFragment.getClass().getSimpleName().equals(
                            SourceLocationPickerFragment.class.getSimpleName())) {
                        startNewBooking();
                    }
                } else if (position == 1) {
                    showLastBooking();
                } else if (position == 2) {
                    utils.startDialActivity(DeliveryActivity.this,
                            ZipRunApp.Constants.CONTACT_NO);
                }
                else if(position == 3){
                    utils.startEmailActivity(DeliveryActivity.this,
                            ZipRunApp.Constants.REPORT_ISSUE_ADDRESS,
                            ZipRunApp.Constants.REPORT_ISSUE_SUBJECT);
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

    }

    private void startNewBooking() {
        Intent intent= getIntent();
        finish();
        startActivity(intent);
    }

    private void showLastBooking(){
        Booking bking = zipRunSession.getBooking();
        if(bking != null){
            booking = bking;
            moveToFragment(new SummaryFragment(), false);

        }else{
            Toast.makeText(this, R.string.error_no_last_booking,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        fragmentManager.removeOnBackStackChangedListener(backStackChangedListener);
        super.onDestroy();
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

        Timber.d("Activity Coming Here");
        int id = item.getItemId();

        switch(id){
            case android.R.id.home: {
                Timber.d("Back Button Pressed in action bar");
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

            Timber.d("Fragment: " + fragment.getClass().getSimpleName() +
                    " Booking " + booking.toJson());

            fragment.getArguments().putString(KEY_BOOKING, booking.toJson());
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();

        }else {
            Timber.d("Nothing on the backstack");
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onBookingUpdated(UpdateBookingEvent ev){
        booking = ev.booking;
    }

    @Subscribe
    public void onSourceLocationSet(OnSourceLocationSet event){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerToggle.setDrawerIndicatorEnabled(false);
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

        contentView.requestTransparentRegion(contentView);
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

        Timber.d("Back Entry Name " + backEntry.getName());

        return fragmentManager.findFragmentByTag(backEntry.getName());
    }

    private void clearBackStack() {
        fragmentManager.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    private void syncNavDrawer() {
        int backStackEntryCount =
                fragmentManager.getBackStackEntryCount();
        drawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }
}


class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private final static int LAYOUT_RES_ID = R.layout.nav_drawer_item;

    private Context context;

    private String []navItems;

    private int  []navIcons =  {
            R.drawable.icon_new_booking,
            R.drawable.icon_track_booking,
            R.drawable.icon_call,
            R.drawable.icon_report_issue,
    };

    public NavigationDrawerAdapter(Context context, String []navItems) {
        super(context, LAYOUT_RES_ID, navItems);
        this.context = context;
        this.navItems = navItems;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(LAYOUT_RES_ID, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.title.setText(navItems[position]);
        holder.imgView.setImageResource(navIcons[position]);
        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.txt_nav_item)
        TextView title;

        @InjectView(R.id.icon_nav_item)
        ImageView imgView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
