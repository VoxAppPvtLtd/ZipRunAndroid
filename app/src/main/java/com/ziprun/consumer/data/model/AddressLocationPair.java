package com.ziprun.consumer.data.model;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

public class AddressLocationPair {
    private static final String TAG = AddressLocationPair.class.getCanonicalName();

    public LatLng latLng;
    public String address;

    public AddressLocationPair(){
        this.latLng = null;
        this.address = null;
    }

    public AddressLocationPair(double latitude, double longitude,
                               String address) {
        if(latitude <= 0 || longitude <= 0){
            this.latLng = null;
        }else
            this.latLng = new LatLng(latitude, longitude);
        this.address = address;
    }

    public String addressAsHtml() {
        return TextUtils.join("<br/>", address.split(", "));
    }
}
