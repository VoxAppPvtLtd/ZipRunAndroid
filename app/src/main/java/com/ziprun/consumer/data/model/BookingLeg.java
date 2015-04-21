package com.ziprun.consumer.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class BookingLeg {
    private static final String TAG = BookingLeg.class.getCanonicalName();


    private AddressLocationPair source;
    private AddressLocationPair destination;
    private RideType rideType;
    private double estimatedDistance;
    private double estimatedCost;
    private long estimatedTime; //Seconds
    private double purchaseAmount;
    private String userInstructions;

    public BookingLeg(AddressLocationPair src){
        source = src;
        destination = new AddressLocationPair();
        rideType = RideType.BUY;
    }

    public AddressLocationPair getSource() {
        return source;
    }

    public void setSource(AddressLocationPair source) {
        this.source = source;
    }

    public AddressLocationPair getDestination() {
        return destination;
    }

    public LatLng getSourceLatLng(){
        return source.latLng;
    }

    public LatLng getDestinationLatLng(){
        return destination.latLng;
    }

    public String getSourceAddress() {
        return source.address;
    }

    public String getDesinationAddress(){
        return destination.address;
    }


    public void setDestination(AddressLocationPair destination) {
        this.destination = destination;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }

    public String getUserInstructions() {
        return userInstructions;
    }

    public void setUserInstructions(String userInstructions) {
        this.userInstructions = userInstructions;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String toJson(){
        return new Gson().toJson(this, this.getClass());
    }

    public static BookingLeg fromJson(String bookingJson){
        return new Gson().fromJson(bookingJson, BookingLeg.class);
    }

}


