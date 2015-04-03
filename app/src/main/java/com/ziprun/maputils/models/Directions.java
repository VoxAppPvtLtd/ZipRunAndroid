package com.ziprun.maputils.models;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Directions {
    private static final String TAG = Directions.class.getCanonicalName();

    public enum StatusCode {
        OK("Success"), NOT_FOUND("Not Found"),
        ZERO_RESULTS("No Results Found"),
        MAX_WAYPOINTS_EXCEEDED("Max Waypoints Exceeded"),
        INVALID_REQUEST("Invalid Request"),
        OVER_QUERY_LIMIT("Over Query Limit"),
        REQUEST_DENIED("Request Denied"),
        UNKNOWN_ERROR("Unknown Error");

        private String status;

        StatusCode(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return this.status;
        }

        public static StatusCode fromStatus(String status){
            for(StatusCode statusCode: StatusCode.values()){
                if(statusCode.toString() == status){
                    return statusCode;
                }
            }
            return null;
        }
    }

    public StatusCode status;
    public String errorMessage;
    public DirectionsRoute[] routes;


    public Directions(String status, String errorMessage,
                      DirectionsRoute[] routes){
        this.errorMessage = errorMessage;
        this.routes = routes;
        this.status = StatusCode.valueOf(status.toUpperCase());
    }


    public void showRoute(int routeIndex, GoogleMap googleMap, int color){
        if (routeIndex >= this.routes.length){
            throw new IndexOutOfBoundsException("Route Index is out of bounds");
        }

        List<LatLng>points = routes[routeIndex].overviewPolyline.decodePath();
        LatLng startPoint = points.get(0);
        LatLng endPoint = points.get(points.size() - 1);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(points.get(0)));

        googleMap.addMarker(new MarkerOptions()
                .position(startPoint));

        googleMap.addMarker(new MarkerOptions()
                .position(endPoint));


        googleMap.addPolyline(new PolylineOptions().addAll(points)).setColor(color);
    }


}
