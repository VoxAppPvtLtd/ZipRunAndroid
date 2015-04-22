package com.ziprun.consumer.data.model;

public class ZipConsumer extends ZipBaseModel{
    private static final String TAG = ZipConsumer.class.getCanonicalName();

    private String mobileNumber;

    private String userID;

    private String apiKey;


    public ZipConsumer(String mobileNumber, String userID, String apiKey){
        this.mobileNumber = mobileNumber;
        this.userID = userID;
        this.apiKey = apiKey;
    }

}
