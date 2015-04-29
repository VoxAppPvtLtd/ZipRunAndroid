package com.ziprun.consumer.data.model;

public class AuthOTP extends ZipBaseModel {
    private static final String TAG = AuthOTP.class.getCanonicalName();

    private String mobileNumber;
    private String otp;

    public AuthOTP(String mobileNumber, String otp){
        this.mobileNumber = mobileNumber;
        this.otp = otp;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
