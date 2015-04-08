package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ziprun.consumer.R;
import com.ziprun.consumer.ui.fragment.LocationPickerFragment;

public class DeliveryActivity extends ZipBaseActivity {

    private LocationPickerFragment locationPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        locationPickerFragment = new LocationPickerFragment();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, locationPickerFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
