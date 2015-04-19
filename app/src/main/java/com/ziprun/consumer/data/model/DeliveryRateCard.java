package com.ziprun.consumer.data.model;

import com.google.gson.Gson;

public class DeliveryRateCard {
    private static final String TAG = DeliveryRateCard.class.getCanonicalName();

    private int ratePerKm;

    private int transactionCost;

    public DeliveryRateCard(int ratePerKm, int transactionCost){
        this.ratePerKm = ratePerKm;
        this.transactionCost = transactionCost;
    }


    public int getRatePerKm() {
        return ratePerKm;
    }

    public int getTransactionCost() {
        return transactionCost;
    }




    public String toJson(){
        return new Gson().toJson(this, this.getClass());
    }

    public static DeliveryRateCard fromJson(String json){
        return new Gson().fromJson(json, DeliveryRateCard.class);
    }


}
