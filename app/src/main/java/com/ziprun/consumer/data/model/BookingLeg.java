package com.ziprun.consumer.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ziprun.consumer.utils.CleanJsonObject;

import java.lang.reflect.Type;

public class BookingLeg extends ZipBaseModel {
    private static final String TAG = BookingLeg.class.getCanonicalName();


    private AddressLocationPair source;
    private AddressLocationPair destination;
    private RideType rideType;
    private double estimatedDistance;
    private double estimatedCost;
    private long estimatedTime; //Seconds
    private double purchaseAmount;
    private String userInstructions;

    public BookingLeg(AddressLocationPair src){
        source = src;
        destination = new AddressLocationPair();
        rideType = RideType.PICKUP;
    }

    public BookingLeg(AddressLocationPair source,
                      AddressLocationPair destination,
                      RideType rideType, String userInstructions,
                      long estimatedTime, double estimatedDistance,
                      double estimatedCost, double purchaseAmount) {

        this.source = source;
        this.destination = destination;
        this.rideType = rideType;
        this.userInstructions = userInstructions;
        this.estimatedCost = estimatedCost;
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
        this.purchaseAmount = purchaseAmount;

    }

    public AddressLocationPair getSource() {
        return source;
    }

    public void setSource(AddressLocationPair source) {
        this.source = source;
    }

    public AddressLocationPair getDestination() {
        return destination;
    }

    public LatLng getSourceLatLng(){
        return source.latLng;
    }

    public LatLng getDestinationLatLng(){
        return destination.latLng;
    }

    public String getSourceAddress() {
        return source.address;
    }

    public String getDestinationAddress(){
        return destination.address;
    }


    public void setDestination(AddressLocationPair destination) {
        this.destination = destination;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }

    public String getUserInstructions() {
        return userInstructions;
    }

    public void setUserInstructions(String userInstructions) {
        this.userInstructions = userInstructions;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public static class BookingLegDeserializer implements
            JsonDeserializer<BookingLeg> {

        @Override
        public BookingLeg deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context)
                throws JsonParseException {

            CleanJsonObject jsonObject = new CleanJsonObject(json
                    .getAsJsonObject());

            CleanJsonObject sourcePoint  = new CleanJsonObject(
                    jsonObject.getJsonObject("source_point"));
            String sourceAddress = jsonObject.getString("source_address", null);

            AddressLocationPair source = new AddressLocationPair(
                    sourcePoint.getDouble("latitude", -1),
                    sourcePoint.getDouble("longitude", -1), sourceAddress);

            CleanJsonObject destinationPoint  = new CleanJsonObject(
                    jsonObject.getJsonObject("destination_point"));
            String destinationAddress = jsonObject.getString("destination_address", null);

            AddressLocationPair destination = new AddressLocationPair(
                    destinationPoint.getDouble("latitude", -1),
                    destinationPoint.getDouble("longitude", -1), destinationAddress);

            RideType rideType = RideType.parse(
                    jsonObject.getString("ride_type", RideType.BUY.rideType));

            String userInstructions = jsonObject.getString("user_instructions", null);

            long estimatedTime = jsonObject.getLong("estimated_time", -1);

            double estimatedDistance = jsonObject.getDouble
                    ("estimated_distance", -1);

            double estimatedCost = jsonObject.getDouble("estimated_cost", -1);

            double purchaseAmount = jsonObject.getDouble("purchase_amount", -1);

            return new BookingLeg(source, destination, rideType,
                    userInstructions, estimatedTime, estimatedDistance,
                    estimatedCost, purchaseAmount);
        }
    }

    public static class BookingLegSerializer implements JsonSerializer<BookingLeg> {

        @Override
        public JsonElement serialize(BookingLeg bookingLeg, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            
            LatLng sourcePoint = bookingLeg.getSourceLatLng();            
            JsonObject sourcePointJson = new JsonObject();
            sourcePointJson.addProperty("latitude", sourcePoint.latitude);
            sourcePointJson.addProperty("longitude", sourcePoint.longitude);
                       
            jsonObject.add("source_point", sourcePointJson);
            jsonObject.addProperty("source_address", bookingLeg.getSourceAddress());

            LatLng destinationPoint = bookingLeg.getDestinationLatLng();
            JsonObject destinationPointJson = new JsonObject();
            destinationPointJson.addProperty("latitude", destinationPoint.latitude);
            destinationPointJson.addProperty("longitude", destinationPoint.longitude);

            jsonObject.add("destination_point", destinationPointJson);
            jsonObject.addProperty("destination_address", 
                    bookingLeg.getDestinationAddress());

            jsonObject.addProperty("ride_type", bookingLeg.getRideType().toString());
            
            jsonObject.addProperty("estimated_time", bookingLeg.getEstimatedTime());

            jsonObject.addProperty("estimated_cost", bookingLeg.getEstimatedCost());

            jsonObject.addProperty("estimated_distance", bookingLeg.getEstimatedDistance());

            jsonObject.addProperty("purchase_amount",
                    bookingLeg.getPurchaseAmount());

            return jsonObject;

        }
    }

}


