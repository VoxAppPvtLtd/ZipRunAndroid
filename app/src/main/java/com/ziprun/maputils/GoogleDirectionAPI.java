package com.ziprun.maputils;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziprun.maputils.models.DateTimeAdapter;
import com.ziprun.maputils.models.Directions;
import com.ziprun.maputils.models.Distance;
import com.ziprun.maputils.models.DistanceAdapter;
import com.ziprun.maputils.models.Duration;
import com.ziprun.maputils.models.DurationAdapter;
import com.ziprun.maputils.models.Fare;
import com.ziprun.maputils.models.FareAdapter;
import com.ziprun.maputils.models.LatLngAdapter;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class GoogleDirectionAPI {
    private static final String TAG = GoogleDirectionAPI.class.getCanonicalName();

    public enum TravelMode {
        DRIVING, WALKING, BICYCLING, TRANSIT, UNKNOWN;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public enum RouteRestriction {
        TOLLS, HIGHWAYS, FERRIES;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

    }

    public enum UnitSystem {
        METRIC, IMPERIAL;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

    }

    public enum TransitMode {
        BUS, SUBWAY, TRAIN, TRAM, RAIL;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }


    public enum TransitRoutingPreference {
        LESS_WALKING, FEWER_TRANSFERS;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    private String origin;

    private String destination;

    private TravelMode travelMode;

    private boolean optimizeWaypoints;

    private String[] wayPoints;

    private boolean alternativeRoutes;

    private TransitMode[] transitModes;

    private TransitRoutingPreference[] transitRoutingPreference;

    private String language;

    private RouteRestriction[] routeRestrictions;

    private UnitSystem unitSystem;

    private String apiKey;

    private String region;

    private GoogleMap googleMap;

    private Date departureTime;

    private Date arrivalTime;

    private HashMap<String, String> urlParamMap;

    public GoogleDirectionAPI(String apiKey, LatLng origin, LatLng dest){
        this(apiKey, convertLatLngToURLParam(origin),
                convertLatLngToURLParam(dest));
    }

    public GoogleDirectionAPI(String apiKey, String origin, String dest){
        this.apiKey = apiKey;
        this.origin = origin;
        this.destination = dest;
        urlParamMap = new HashMap<String, String>();
        urlParamMap.put("origin", this.origin);
        urlParamMap.put("destination", this.destination);
        urlParamMap.put("key", this.apiKey);
    }

    public GoogleDirectionAPI mode(TravelMode travelMode){
        this.travelMode = travelMode;
        urlParamMap.put("mode", travelMode.toString());
        return this;
    }

    public GoogleDirectionAPI optimizeWaypoints(boolean optimize) {
        optimizeWaypoints = optimize;
        if (wayPoints != null) {
            return waypoints(wayPoints);
        } else {
            return this;
        }
    }

    public GoogleDirectionAPI waypoints(String... waypoints) {
        if (waypoints == null || waypoints.length == 0) {
            return this;
        } else if (waypoints.length == 1) {
            urlParamMap.put("waypoints", waypoints[0]);
        } else {
             urlParamMap.put("waypoints", (optimizeWaypoints ? "optimize:true|" : "") +
                     TextUtils.join("|", waypoints));
        }

        return this;
    }

    public GoogleDirectionAPI alternatives(boolean alternatives){
        this.alternativeRoutes = alternatives;
        urlParamMap.put("alternatives", alternatives ? "true" : "false");
        return this;
    }


    public GoogleDirectionAPI avoid(RouteRestriction... restrictions){
        this.routeRestrictions = restrictions;
        urlParamMap.put("avoid", TextUtils.join("|", restrictions));
        return this;
    }

    public GoogleDirectionAPI language(String langCode){
        this.language = langCode;
        urlParamMap.put("language", langCode);
        return this;
    }

    public GoogleDirectionAPI region(String regionCode){
        this.region = regionCode;
        urlParamMap.put("region", regionCode);
        return this;
    }

    public GoogleDirectionAPI units(UnitSystem unitSystem){
        this.unitSystem = unitSystem;
        urlParamMap.put("units", unitSystem.toString());
        return this;
    }

    public static String convertLatLngToURLParam(LatLng latlng){
        return latlng.latitude + "," + latlng.longitude;
    }

    /**
     * Specifies one or more preferred modes of transit. This parameter may only be specified for
     * requests where the mode is transit.
     */
    public GoogleDirectionAPI transitMode(TransitMode... transitModes) {
        this.transitModes = transitModes;
        urlParamMap.put("transit_mode", TextUtils.join("|", transitModes));
        return this;
    }

    /**
     * Specifies preferences for transit requests. Using this parameter,
     * you can bias the options returned, rather than accepting the default best route chosen by
     * the API.
     */
    public GoogleDirectionAPI transitRoutingPreference
    (TransitRoutingPreference... prefs) {
        this.transitRoutingPreference = prefs;
        urlParamMap.put("transit_routing_preference", TextUtils.join("|", prefs));
        return this;
    }


    public Observable<Directions> getDirections() {
        Log.i(TAG, "Get Direction Method Called");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
                .registerTypeAdapter(Distance.class, new DistanceAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Fare.class, new FareAdapter())
                .registerTypeAdapter(LatLng.class, new LatLngAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api")
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog(TAG))
                .build();

           return restAdapter.create(GoogleDirectionService.class).getDirections(urlParamMap);
    }


//    public Observable<>


}
