package com.ziprun.consumer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.OAuthSigning;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.ziprun.consumer.R;

import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class LoginActivity extends ZipBaseActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "al9udcuqmC6j6XQQnal6HigDd";
    private static final String TWITTER_SECRET = "NMvBd9Jz2BQsWZS7xKtPROcJcOcCicqCn7Hyhk5CCEMpoxYFn2";

    public static final String TAG = LoginActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY,
                TWITTER_SECRET);
        Fabric.with(this, new Digits(), new Crashlytics(), new Twitter(authConfig));

        Digits.authenticate(new AuthCallback() {
            @Override
            public void success(DigitsSession session,
                                String phoneNumber) {
                // Do something with the session
                TwitterAuthToken authToken = (TwitterAuthToken) session.getAuthToken();

                OAuthSigning oauthSigning = new OAuthSigning(authConfig, authToken);

                Map authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();

                Log.i(TAG, phoneNumber + " " + session.getId() + " " +
                    (String) authHeaders.get("X-Auth-Service-Provider") + " : "
                    + (String) authHeaders.get("X-Verify-Credentials-Authorization")
                );

                zipRunSession.authenticatUser();
                startDeliveryActivity();
            }

            @Override
            public void failure(DigitsException exception) {
                Toast.makeText(LoginActivity.this,
                        R.string.msg_authentication_failure, Toast.LENGTH_LONG).show();
            }
        }, R.style.Theme_ZipRunDigit);
    }

    private void startDeliveryActivity() {
        startActivity(new Intent(this, DeliveryActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
