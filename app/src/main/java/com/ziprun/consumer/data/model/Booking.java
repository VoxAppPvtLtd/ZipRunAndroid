package com.ziprun.consumer.data.model;

import java.util.ArrayList;

public class Booking extends ZipBaseModel{
    private static final String TAG = Booking.class.getCanonicalName();
    private int rateID;
    private ArrayList<BookingLeg> bookingLegs;

    public Booking(){
        bookingLegs = new ArrayList<>();
    }

    public void addBookingLeg(AddressLocationPair src){
        bookingLegs.add(new BookingLeg(src));
    }

    public void setRateID(int rateID){
        this.rateID = rateID;
    }

    public int getLegsCount(){
        return bookingLegs.size();
    }


    public BookingLeg getBookingLeg(int legIndex) {
        if(legIndex >= bookingLegs.size()){
            throw new IndexOutOfBoundsException("Leg index cannot be greater " +
                    "then total legs in the ride");
        }
        return bookingLegs.get(legIndex);

    }

}
