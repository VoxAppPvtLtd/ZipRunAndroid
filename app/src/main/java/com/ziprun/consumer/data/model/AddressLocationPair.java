package com.ziprun.consumer.data.model;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

public class AddressLocationPair {
    private static final String TAG = AddressLocationPair.class.getCanonicalName();

    public LatLng latLng;
    public String address;

    public String addressAsHtml() {
        return TextUtils.join("<br/>", address.split(", "));
    }
}
