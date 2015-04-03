package com.ziprun.maputils;

import com.ziprun.maputils.models.Directions;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

public interface GoogleDirectionService {
    @GET("/directions/json")
    Observable<Directions> getDirections(@QueryMap Map<String, String> options);
}
