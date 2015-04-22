package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.DeliveryRateCard;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends ZipBaseActivity {
    private static final String TAG = SplashActivity.class.getCanonicalName();
    private static final int SPLASH_ACTIVITY_WAIT = 5000    ; //5 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DeliveryRateCard rateCard = new DeliveryRateCard(1,     2, 20, 9, 2);
        zipRunSession.setRateCard(rateCard);
        checkNetworkConnectivity();

    }

    private void switchActivity(){
        Intent intent;
        if(zipRunSession.isUserAuthenticated()){
            intent = new Intent(this, DeliveryActivity.class);
        }else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private void checkNetworkConnectivity() {
        final boolean isOnline = utils.isOnline();
        if(!isOnline)
            Toast.makeText(this, "No Internet Connectivity. Please try " +
                    "again", Toast.LENGTH_LONG).show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(isOnline){
                    Log.i(TAG, "Internet connectivity is there");
                    switchActivity();
                }else{
                    finish();
                }
            }
        }, SPLASH_ACTIVITY_WAIT);
    }
}
