package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ziprun.consumer.R;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class SplashActivity extends ZipBaseActivity {
    private static final String TAG = SplashActivity.class.getCanonicalName();
    private static final int SPLASH_ACTIVITY_WAIT = 5000    ; //5 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppsFlyerLib.sendTracking(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnectivity();

    }

    private void switchActivity(){
        Intent intent;
        if(zipRunSession.isUserAuthenticated()){
            intent = new Intent(this, DeliveryActivity.class);
        }else {
            intent = new Intent(this, IntroActivity.class);
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
                    Timber.d("Internet connectivity is there");
                    switchActivity();
                }else{
                    finish();
                }
            }
        }, SPLASH_ACTIVITY_WAIT);
    }
}
