package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ziprun.consumer.R;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeliveryActivity extends ZipBaseActivity {

    private LocationPickerFragment locationPickerFragment;

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


        if (savedInstanceState == null) {
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

            if(resultCode == RESULT_OK){
                    locationPickerFragment.checkLocationSettings();
            }else{
                locationPickerFragment.isLocationEnabled(false);
            }
        }


    }
}
