package com.ziprun.consumer.data.model;

import com.google.gson.annotations.SerializedName;

public class DeliveryRateCard  extends ZipBaseModel{
    private static final String TAG = DeliveryRateCard.class.getCanonicalName();

    @SerializedName("rate_id")
    private int rateID;
    private int minDistance;
    private int minPrice;
    private int ratePerKm;
    private int transactionCost;

    public DeliveryRateCard(int rateID, int minDistance, int minPrice,
                            int ratePerKm, int transactionCost){
        this.rateID = rateID;
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


    public int getRateID() {
        return rateID;
    }

}
