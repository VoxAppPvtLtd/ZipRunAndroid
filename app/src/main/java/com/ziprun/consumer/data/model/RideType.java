package com.ziprun.consumer.data.model;

public enum RideType {
    BUY("Buy"), PICKUP("Pickup");

    public String rideType;

    RideType(String bookingType){
        this.rideType = bookingType;
    }

    @Override
    public String toString() {
        return this.rideType;
    }

    public static RideType parse(String rideType){
        for(RideType rd: RideType.values()){
            if(rd.rideType.toUpperCase() == rideType.toUpperCase())
                return rd;
        }
        return null;
    }
}
