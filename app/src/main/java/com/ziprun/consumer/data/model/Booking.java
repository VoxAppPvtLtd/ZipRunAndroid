package com.ziprun.consumer.data.model;

import com.google.gson.annotations.SerializedName;
import com.ziprun.consumer.utils.Exclude;

import java.util.ArrayList;

public class Booking extends ZipBaseModel{
    private static final String TAG = Booking.class.getCanonicalName();

    @Exclude
    private DeliveryRateCard rateCard;

    @SerializedName("rate")
    private int rateID;

    @Exclude boolean submitted;

    @SerializedName("legs")
    private ArrayList<BookingLeg> bookingLegs;

    public Booking(){
        bookingLegs = new ArrayList<>();
    }

    public void addBookingLeg(AddressLocationPair src){
        bookingLegs.add(new BookingLeg(src));
    }

    public void setRateCard(DeliveryRateCard rateCard){
        this.rateCard = rateCard;
        this.rateID = rateCard.getRateID();
    }

    public void setSubmitted(){
        this.submitted = true;
    }

    public boolean isSubmitted(){
        return submitted;
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

    public DeliveryRateCard getRateCard() {
        return rateCard;
    }
}
