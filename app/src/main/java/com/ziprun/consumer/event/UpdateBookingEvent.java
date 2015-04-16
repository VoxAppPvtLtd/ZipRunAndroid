package com.ziprun.consumer.event;

import com.ziprun.consumer.data.model.Booking;

public class UpdateBookingEvent {
    private static final String TAG = UpdateBookingEvent.class.getCanonicalName();

    public Booking booking;

    public UpdateBookingEvent(Booking booking) {
        this.booking = booking;
    }
}
