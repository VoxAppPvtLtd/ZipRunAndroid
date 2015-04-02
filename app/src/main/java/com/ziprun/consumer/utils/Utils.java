package com.ziprun.consumer.utils;

import android.location.Address;
import android.text.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    @Inject
    public Utils() {
    }


    public String addressToString(Address address){
        ArrayList<String> addressFragments = new ArrayList<String>(
                address.getMaxAddressLineIndex());

        for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }

        return TextUtils.join(System.getProperty("line.separator"),
                addressFragments);
    }
}
