package com.ziprun.consumer.data.model;

import com.google.gson.Gson;

public class DeliveryRateCard {
    private static final String TAG = DeliveryRateCard.class.getCanonicalName();

    private int rateID;
    private int minDistance;
    private int minPrice;
    private int ratePerKm;

    private int transactionCost;

    public DeliveryRateCard(int minDistance, int minPrice, int ratePerKm,
                            int transactionCost){
        this.minDistance = minDistance;
        this.minPrice = minPrice;
        this.ratePerKm = ratePerKm;
        this.transactionCost = transactionCost;
    }

    public int getRatePerKm() {
        return ratePerKm;
    }

    public int getTransactionCost() {
        return transactionCost;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public String toJson(){
        return new Gson().toJson(this, this.getClass());
    }

    public static DeliveryRateCard fromJson(String json){
        return new Gson().fromJson(json, DeliveryRateCard.class);
    }


}
