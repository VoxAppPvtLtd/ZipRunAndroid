package com.ziprun.consumer.data.model;

public class ZipConsumer extends ZipBaseModel{
    private static final String TAG = ZipConsumer.class.getCanonicalName();

    private String mobileNumber;

    private String consumerId;

    private String token;


    public ZipConsumer(String mobileNumber, String consumerId, String token){
        this.mobileNumber = mobileNumber;
        this.consumerId = consumerId;
        this.token = token;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getToken() {
        return token;
    }

}
