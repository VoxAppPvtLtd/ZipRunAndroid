package com.ziprun.consumer.presenter;

import android.support.annotation.Nullable;

import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.event.OnBookingInstructionSet;
import com.ziprun.consumer.event.UpdateBookingEvent;
import com.ziprun.consumer.ui.fragment.InstructionFragment;

public class InstructionPresenter extends DeliveryPresenter {
    private static final String TAG = InstructionPresenter.class.getCanonicalName();

    private static final int BUY_TAB_POS = 0;
    private static final int PICKUP_TAB_POS = 1;

    protected InstructionFragment instructionView;

    public InstructionPresenter(InstructionFragment view) {
        super(view);
        instructionView = view;
    }

    @Override
    public void setBooking(@Nullable String bookingJson) {
        super.setBooking(bookingJson);
    }

    @Override
    public void start() {
        super.start();
        if(booking.getBookingType() == Booking.BookingType.BUY){
            instructionView.changeTab(BUY_TAB_POS);
        }else {
            instructionView.changeTab(PICKUP_TAB_POS);
        }
    }

    @Override
    public void stop() {
        super.stop();
        updateBooking();
    }

    public void moveForward(){
        updateBooking();
        bus.post(new OnBookingInstructionSet());
    }

    public void updateBooking(){
        int currentTab = instructionView.getCurrentTab();
        booking.setBookingType(currentTab == BUY_TAB_POS ? Booking
                .BookingType.BUY : Booking.BookingType.PICKUP);
        booking.setInstructions(instructionView.getCurrentInstruction());

        bus.post(new UpdateBookingEvent(booking));
    }
}
