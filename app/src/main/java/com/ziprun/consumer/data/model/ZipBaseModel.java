package com.ziprun.consumer.data.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ZipBaseModel {
    private static final String TAG = ZipBaseModel.class.getCanonicalName();

    protected static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy
                    .LOWER_CASE_WITH_UNDERSCORES).create();

    public String toJson(){
        return gson.toJson(this, this.getClass());
    }

    public static <T> T fromJson(String json, Class<T> clz){
        return gson.fromJson(json, clz);
    }

}
