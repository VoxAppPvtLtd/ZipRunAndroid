package com.ziprun.consumer.data.model;

import com.google.gson.Gson;

public class Booking {
    private static final String TAG = Booking.class.getCanonicalName();

    public enum BookingType {
        BUY("buy"), PICKUP("pickup");

        public String bookingType;

        private BookingType(String bookingType){
            this.bookingType = bookingType;
        }

        @Override
        public String toString() {
            return this.bookingType;
        }
    }

    private AddressLocationPair sourceLocation;
    private AddressLocationPair destLocation;
    private BookingType bookingType;
    private double estimateDistance;
    private double estimateCost;
    private String notes;

    public Booking(){
        sourceLocation = new AddressLocationPair();
        destLocation = new AddressLocationPair();
        bookingType = BookingType.BUY;
    }

    public AddressLocationPair getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(AddressLocationPair sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public AddressLocationPair getDestLocation() {
        return destLocation;
    }

    public void setDestLocation(AddressLocationPair destLocation) {
        this.destLocation = destLocation;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(double estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public double getEstimateCost() {
        return estimateCost;
    }

    public void setEstimateCost(double estimateCost) {
        this.estimateCost = estimateCost;
    }

    public String toJson(){
        return new Gson().toJson(this, this.getClass());
    }

    public static Booking fromJson(String bookingJson){
        return new Gson().fromJson(bookingJson, Booking.class);
    }

}


