package com.ziprun.consumer.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ziprun.consumer.ForApplication;
import com.ziprun.consumer.data.model.DeliveryRateCard;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class ZipRunSession {
    private static final String TAG = ZipRunSession.class.getCanonicalName();

    private SharedPreferences preferences;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Shared Preferences mode
    static int PRIVATE_MODE = 0;

    static final String PREF_NAME = "ZipSessionPreference";

    private static final String KEY_IS_AUTHENTICATED = "is_authenticated";

    private static final String KEY_RATE_CARD = "rate_card";

    Context appContext;

    @Inject
    public ZipRunSession(@ForApplication Context context) {
        appContext = context;
        preferences = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public boolean isUserAuthenticated(){
        return preferences.getBoolean(KEY_IS_AUTHENTICATED, false);
    }

    public void setRateCard(DeliveryRateCard rateCard){
        preferences.edit().putString(KEY_RATE_CARD, rateCard.toJson()).apply();
    }

    public DeliveryRateCard getRateCard(){
        return DeliveryRateCard.fromJson(
                preferences.getString(KEY_RATE_CARD, null), DeliveryRateCard.class);
    }

    public void authenticatUser() {
        preferences.edit().putBoolean(KEY_IS_AUTHENTICATED, true).apply();
    }
}
