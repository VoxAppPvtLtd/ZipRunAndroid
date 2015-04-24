package com.ziprun.consumer.event;

public class BookingSubmissionStatus {
    private static final String TAG = BookingSubmissionStatus.class.getCanonicalName();

    public boolean success;
    public BookingSubmissionStatus(boolean b) {
        success = b;
    }
}
