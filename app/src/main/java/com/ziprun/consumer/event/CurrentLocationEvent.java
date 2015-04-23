package com.ziprun.consumer.event;

import android.location.Location;

public class CurrentLocationEvent {
    private static final String TAG = CurrentLocationEvent.class.getCanonicalName();
    public Location currentLocation;

    public CurrentLocationEvent(Location location){
        this.currentLocation = location;
    }
}
