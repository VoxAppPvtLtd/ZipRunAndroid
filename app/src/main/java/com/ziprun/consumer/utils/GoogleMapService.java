package com.ziprun.consumer.utils;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleMapService {
    @GET("/geocode/json")
    void reverseGeocode(@Query("latlng") String latlng,
                    @Query("key") String apiKey, Callback<JSONObject> callback);
}
