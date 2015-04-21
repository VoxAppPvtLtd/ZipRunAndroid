package com.ziprun.consumer.data.model;

public enum RideType {
    BUY("buy"), PICKUP("pickup");

    public String rideType;

    RideType(String bookingType){
        this.rideType = bookingType;
    }

    @Override
    public String toString() {
        return this.rideType;
    }
}
