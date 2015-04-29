package com.ziprun.consumer.presenter;

import com.ziprun.consumer.data.model.RideType;
import com.ziprun.consumer.event.OnBookingInstructionSet;
import com.ziprun.consumer.ui.fragment.InstructionFragment;

public class InstructionPresenter extends DeliveryPresenter {
    private static final String TAG = InstructionPresenter.class.getCanonicalName();

    private static final int BUY_TAB_POS = 1;
    private static final int PICKUP_TAB_POS = 0;

    protected InstructionFragment instructionView;

    public InstructionPresenter(InstructionFragment view) {
        super(view);
        instructionView = view;
    }

    @Override
    public void start() {
        super.start();
        if(bookingLeg.getRideType() == RideType.BUY){
            instructionView.changeTab(BUY_TAB_POS);
        }else {
            instructionView.changeTab(PICKUP_TAB_POS);
        }
    }

    public void moveForward(){
        updateBooking();
        bus.post(new OnBookingInstructionSet());
    }

    public void updateBooking(){
        int currentTab = instructionView.getCurrentTab();
        bookingLeg.setRideType(currentTab == BUY_TAB_POS ? RideType.BUY : RideType.PICKUP);
        bookingLeg.setUserInstructions(instructionView.getCurrentInstruction());
        super.updateBooking();
    }
}
