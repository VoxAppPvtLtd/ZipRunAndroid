package com.ziprun.consumer.utils;

import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.maps.model.LatLng;
import com.ziprun.consumer.ForApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    @Inject
    public Utils() {
    }

    @Inject
    Display display;

    @ForApplication
    @Inject
    Context appContext;


    public String addressToString(Address address){
        return addressToString(address, System.getProperty("line.separator"));
    }

    public String addressToString(Address address, CharSequence delim){
        ArrayList<String> addressFragments = new ArrayList<String>(
                address.getMaxAddressLineIndex());

        for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }

        return TextUtils.join(delim, addressFragments);
    }

    public LatLng getLatLngFromLocation (Location loc){
        return  new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    public int getScreenHeight(){
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }

    public int getScreenWidth(){
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    /**
     * This method convets dp unit to equivalent device specific value in
     * pixels.
     *
     * @param dp      A value in dp(Device independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to dp according to
     *         device
     */
    public int convertDpToPixel(float dp, Context context) {
        return Math.round(dp
                * context.getResources().getDisplayMetrics().density);
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public float convertPixelsToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;

    }

    public int getColor(int resID) {
        return appContext.getResources().getColor(resID);
    }


    public boolean isOnline(){
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        try {
            ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = 0;
            exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (InterruptedException e) {
            Log.e(TAG, "Unable to check if app is online", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to check if app is online", e);
        }
        return false;
    }

    public float calculateDistance(LatLng source, LatLng dest){
        float[] results = new float[1];
        Location.distanceBetween(source.latitude, source.longitude,
                dest.latitude, dest.longitude, results);

        return results[0];
    }

    public String formatAddressAsHtml(String address) {
        String[] addComps = address.split(", ");
        List<String> formattedAddress = new ArrayList<>();

        for (int i = addComps.length - 1; i >= 0; i = i - 2) {
            if (i > 0)
                formattedAddress.add(String.format("%s, %s",
                        addComps[i - 1], addComps[i]));
            else {
                formattedAddress.add(addComps[i]);
            }
        }
        Collections.reverse(formattedAddress);
        return TextUtils.join("<br/>", formattedAddress);
    }

    public String getCityFromAddress(String address){
        String []addressParts = address.split(",");
        String lastPart = addressParts[addressParts.length - 1].trim();
        if(lastPart.equals("India")){
            return addressParts[addressParts.length - 2].trim().split(" ")[0];
        }else{
            return lastPart.split(" ")[0];
        }
    }

}
